package edu.nudt.xtrace;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

public class Trace{
	private ArrayList<Report> reports;
	private ArrayList<Report> edges;
	private ArrayList<Report> roots;
	private String taskID;
	private double delay;//整个task的延迟，在xtrace中生成根节点的结束时间不好获取，设置为long.max_value，是不准确的
				//在xtrace服务器端，根据接受的报文信息在tasks表中更改为正确的时间，但reports表中没改
	private Connection conn;
	public ArrayList<Report> getRoots(){return roots;}
	public ArrayList<Report> getReports(){return reports;}
	public Trace(){}
	private void init(Connection conn, String taskID, String delay)
	{
		reports = new ArrayList<Report>();
		edges = new ArrayList<Report>();
		roots = new ArrayList<Report>();
		this.conn = conn;
		this.taskID = taskID;
		try{
			this.delay = Double.parseDouble(delay);
		}catch(Exception e){
			this.delay = Double.MAX_VALUE;
		}
	}
	private boolean loadData()//load reports and edges from mysql
	{
		try{
			PreparedStatement getReportsByTaskID = 
					conn.prepareStatement("select * from Report where TaskID = ?");
			PreparedStatement getEdgesByTaskID = 
					conn.prepareStatement("select * from Edge where TaskID = ?");
		
			getReportsByTaskID.setString(1, taskID);
			ResultSet rs = getReportsByTaskID.executeQuery();
			ResultSetMetaData meta = rs.getMetaData();
			int column1 = meta.getColumnCount();
			while(rs.next())
			{
				Report r = new Report();
				for(int i =1;i<=column1; i++)
					r.put(meta.getColumnName(i).toLowerCase(), rs.getString(i));
				reports.add(r);
			}
			rs.close();

			getEdgesByTaskID.setString(1, taskID);
			rs = getEdgesByTaskID.executeQuery();
			meta = rs.getMetaData();
			column1 = meta.getColumnCount();
			while(rs.next())
			{
				Report r = new Report();
				for(int i =1;i<=column1; i++)
					r.put(meta.getColumnName(i).toLowerCase(), rs.getString(i));
				edges.add(r);
			}
			rs.close();
		}catch (SQLException e) {
			return false;
		}
		
		preProcess();
		
		for(int i=0; i<reports.size(); i++)
		{
			Report rep = reports.get(i);
			rep.put("index", String.valueOf(i));//为每个report编号，以唯一识别
			//为每一report计算延迟，若比delay大，则用delay设置，否则用开始时间减去结束时间
			if(rep.get("starttime")==null || rep.get("endtime")==null)
				continue;
			String st = rep.get("starttime").get(0);
			String et = rep.get("endtime").get(0);
			Long lst=Long.parseLong(st);
			Long let=Long.parseLong(et);
			if(lst==let){
				let=let+1;
				rep.remove("endtime");
				rep.put("endtime",String.valueOf(let));
			}
			double tempDelay = (double)(let- lst)/1000000;
			if(tempDelay > delay)
				rep.put("delay",String.valueOf(delay));
			else
				rep.put("delay",String.valueOf(tempDelay));
		}

		return true;
	}
	//主要处理完全相同的两个Rep和Edge
	private void preProcess(){
		for(int i=0; i<reports.size()-1; i++){
			for(int j=i+1; j<reports.size(); j++){
				Report rep1 = reports.get(i);
				Report rep2 = reports.get(j);
				String strRep1 = rep1.toString();
				String strRep2 = rep2.toString();
				if(strRep1.equals(strRep2)){
					reports.remove(j);
					j--;
				}
			}
		}
		for(int i=0; i<edges.size()-1; i++){
			for(int j=i+1; j<edges.size(); j++){
				Report edges1 = edges.get(i);
				Report edges2 = edges.get(j);
				String strEdge1 = edges1.toString();
				String strEdge2 = edges2.toString();
				if(strEdge1.equals(strEdge2)){
					edges.remove(j);
					j--;
				}
			}
		}
	}
	
	private void partTreeFindFather(int j)//同一部分树找father
	{
		for(int i=0; i<reports.size(); i++)
		{
			if(i == j)//同一节点
				continue;

			Report father = reports.get(i);
			Report son = reports.get(j);

			if(son.get("tid") == null)
				return;
			if(father.get("tid") == null)
				continue;
			String sid = son.get("tid").get(0);
			String fid = father.get("tid").get(0);
			if(!sid.equals(fid))//不在同一部分树
				continue;

			if(son.get("starttime")==null || son.get("endtime")==null)
				return;
			long sonST  = Long.parseLong(son.get("starttime").get(0));
			long sonET  = Long.parseLong(son.get("endtime").get(0));
			if(sonST > sonET)//应该不会出现这种情况
				return;
			if(father.get("starttime")==null || father.get("endtime")==null)
				continue;
			long fatherST  = Long.parseLong(father.get("starttime").get(0));
			long fatherET  = Long.parseLong(father.get("endtime").get(0));
			if(fatherST > fatherET)
				continue;

			if(fatherST <= sonST && sonET <= fatherET)//是父节点或父父节点或
			{
				if(son.get("father")==null)//还没有父节点
					son.put("father", father.get("index").get(0));
				else{//比谁更像父节点，开始结束时间挨得更近的更可能是
					int existFatherID =  Integer.parseInt(son.get("father").get(0));
					Report existFather = reports.get(existFatherID);
					long existFatherST  = Long.parseLong(existFather.get("starttime").get(0));
					long existFatherET  = Long.parseLong(existFather.get("endtime").get(0));
					if(existFatherST <= fatherST && fatherET <= existFatherET)
					{
						son.remove("father");
						son.put("father", father.get("index").get(0));
					}
				}
			}
			
		}
	}
	private void partTreeFindSon()//根据父节点信息，恢复子节点信息
	{
		for(int i=0; i<reports.size(); i++)
		{
			Report son = reports.get(i);
			if(son.get("father")==null)
				continue;
			int fatherID = Integer.parseInt(son.get("father").get(0));
			Report father = reports.get(fatherID);
			father.put("children", son.get("index").get(0));
		}
	}
	private void findPartTreeTopology()//部分树之间找父子关系
	{
		for(int i=0;i<edges.size();i++)//对每一条边
		{
			Report edge = edges.get(i);
			if(edge.get("fathertid")==null || edge.get("fatherstarttime")==null || edge.get("childtid")==null)
				continue;
			String fatherTID = edge.get("fathertid").get(0);
			String fatherST = edge.get("fatherstarttime").get(0);
			String childTID = edge.get("childtid").get(0);
			int fatherIndex = -1;
			for(int j=0; j<reports.size(); j++)//找边对应父节点，唯一
			{
				Report rep = reports.get(j);
				if(rep.get("tid")==null || rep.get("starttime")==null)
					continue;
				String TID = rep.get("tid").get(0);
				String st  = rep.get("starttime").get(0);
				if(fatherTID.equals(TID) && fatherST.equals(st))
				{
					fatherIndex = j;
					break;
				}
			}
			if(fatherIndex == -1)
				continue;
			Report father = reports.get(fatherIndex);
			for(int j=0; j< reports.size(); j++)//找子节点，很多，对应部分树的所有root
			{
				Report son = reports.get(j);
				if(son.get("tid")==null)
					continue;
				String sonTID = son.get("tid").get(0);
				int sonIndex = Integer.parseInt(son.get("index").get(0));
				if(son.get("father")==null && sonTID.equals(childTID))
				{
					son.put("father",String.valueOf(fatherIndex));
					father.put("children", String.valueOf(sonIndex));
				}
			}
		}
	}
	void findRoots()//调用树的root
	{
		for(int i=0;i<reports.size();i++)
		{
			Report rep = reports.get(i);
			if(rep.get("father") == null)//没有father的就是
				roots.add(rep);
		}
	}
	private void sortChildren()//给子节点排序，根据starttime排序
	{
		for(int i=0;i<reports.size();i++)
		{
			Report rep = reports.get(i);
			List<String> children = rep.get("children");
			if(children == null) continue;
			for(int j = 0; j<children.size(); j++){
				for(int k = j+1; k<children.size(); k++)
				{
					int indexj = Integer.parseInt(children.get(j));
					int indexk = Integer.parseInt(children.get(k));
					if(indexj>=reports.size() || indexk>=reports.size())
						continue;
					Report sonj = reports.get(indexj);
					Report sonk = reports.get(indexk);
					long startTimej = Long.parseLong(sonj.get("starttime").get(0));
					long startTimek = Long.parseLong(sonk.get("starttime").get(0));
					if(startTimej > startTimek)
					{
						String tmp = children.get(j);
						children.set(j, children.get(k));
						children.set(k, tmp);
					}
				}
			}
			rep.remove("children");
			for(int j=0;j<children.size();j++)
			{
				rep.put("children",children.get(j));
			}
		}

		//给根节点排序
		for(int i=0;i<roots.size();i++)
			for(int j=i+1;j<roots.size();j++)
			{
				Report rooti=roots.get(i);
				Report rootj=roots.get(j);
				long startTimei=Long.parseLong(rooti.get("starttime").get(0));
				long startTimej=Long.parseLong(rootj.get("starttime").get(0));
				if(startTimei>startTimej){
					roots.set(i,rootj);
					roots.set(j,rooti);
				}
			}
	}
	public ArrayList<Report> findTopology(Connection conn, String taskID, String delay)//建立父子关系
	{
		init(conn, taskID, delay);//初始化
		if(!loadData()) return null;//从数据库加载数据
		for(int i=0; i<reports.size(); i++) partTreeFindFather(i);//在同一部分根据开始结束时间树中找父节点
		partTreeFindSon();//在同一部分树中根据父节点找子节点
		findPartTreeTopology();//根据边的信息构建部分树之间的关系
		findRoots();
		sortChildren();
		return reports;
	}
	//这种方法生成的graphviz图不能保证顺序
	/*public boolean genTxt(String path, String direction, String shape)//将调用树信息转换为dot文件，存在path中
	{
		try {
			File file = new File(path);  
			if(!file.getParentFile().exists()){  
				file.getParentFile().mkdirs();
			}  
			if(!file.exists()){  
				file.createNewFile();
			}  
			//写入txt文件    
			FileWriter fileWriter = new FileWriter(path,false);  
			BufferedWriter bw = new BufferedWriter(fileWriter);  
			
			bw.write("digraph G {\n");
			bw.write("\trankdir = " + direction + ";\n");
			bw.write("\tnode [fontsize=\"9\", shape = " + shape + "]\n");
			bw.write("\tedge [fontsize=\"9\"]\n\n");
			for(int i=0;i<reports.size();i++)
			{
				bw.write("\t");
				Report rep = reports.get(i);
				String d = rep.get("delay").get(0);
				bw.write("\""+rep.get("index").get(0)+"\"");
				String temp=rep.get("children")==null?"[]":rep.get("children").toString();//
				bw.write(" [label=\""+rep.get("index").get(0)+":"+rep.get("opname").get(0)+"\\n"+temp+"\\n"+rep.get("agent").get(0)+"\\n"
					+rep.get("hostname").get(0)+"\\n"+rep.get("starttime").get(0)+"\\n"+
					String.format("%.2f", Double.parseDouble(d))+"ms\"]\n");
				List<String> children = rep.get("children");
				if(children == null) {bw.write("\n");continue;}
				for(int j=0; j<children.size(); j++)
				{
					bw.write("\t\t");
					String son = children.get(j);
					bw.write("\""+rep.get("index").get(0)+"\"->\""+son+"\" [color=\"black\"]\n");
				}
				bw.write("\n");
			}
			bw.write("}");
			fileWriter.flush();  
			bw.close();  
			fileWriter.close();
		} catch (Exception e) {  
			return false;
		} 
		return true;
	}*/

	public boolean genTxt(String path, String direction, String shape)//将调用树信息转换为dot文件，存在path中
	{
		try {
			File file = new File(path);  
			if(!file.getParentFile().exists()){  
				file.getParentFile().mkdirs();
			}  
			if(!file.exists()){  
				file.createNewFile();
			}  
			//写入txt文件    
			FileWriter fileWriter = new FileWriter(path,false);  
			BufferedWriter bw = new BufferedWriter(fileWriter);  
			
			
			/*bw.write("test\n");
			for(int i=0; i<reports.size()-1; i++){
				Report rep = reports.get(i);
				String strRep = rep.toString();
				bw.write(strRep+"\n");
			}*/
		
			bw.write("digraph G {\n");
			bw.write("\trankdir = " + direction + ";\n");
			bw.write("\tnode [fontsize=\"9\", shape = " + shape + "]\n");
			bw.write("\tedge [fontsize=\"9\"]\n\n");
			for(int i=0;i<roots.size();i++)
				genSubTree(roots.get(i),bw);
			bw.write("}");
			fileWriter.flush();  
			bw.close();  
			fileWriter.close();
		} catch (Exception e) {  
			return false;
		} 
		return true;
	}
	private void genSubTree(Report rep, BufferedWriter bw) throws IOException
	{
		bw.write("\t");
		String d = rep.get("delay").get(0);
		bw.write("\"node_"+rep.get("index").get(0)+"\"");
		//String temp=rep.get("children")==null?"[]":rep.get("children").toString();//
		bw.write(" [label=\""+/**rep.get("index").get(0)+":"+**/rep.get("opname").get(0)+"\\n"+/**temp+"\\n"+**/rep.get("agent").get(0)+"\\n"
			+rep.get("hostname").get(0)+"\\n"+/**rep.get("starttime").get(0)+"\\n"+**/
			String.format("%.2f", Double.parseDouble(d))+"ms\"]\n");
		List<String> children = rep.get("children");
		if(children == null) return;
		for(int j=0; j<children.size(); j++)
		{
			int index = Integer.parseInt(children.get(j));
			genSubTree(reports.get(index),bw);
		}
		for(int j=0; j<children.size(); j++)
		{
			bw.write("\t\t");
			String son = children.get(j);
			bw.write("\"node_"+rep.get("index").get(0)+"\"->\"node_"+son+"\" [color=\"black\"]\n");
		}
		
	}
	public boolean genAbnormalTxt(String path, DiagnosisResult dr)//生成异常trace的dot文件，用红色标记异常op
	{
		String direction = "TB";
		String shape = "ellipse";
		if(dr==null) return genTxt(path, direction, shape);//若异常信息为null，则生成正常点图
		if(reports==null)return false;
		if(reports.get(0)==null)return false;
		if(reports.get(0).get("dfsorder")==null)depthFirstSearchOrder();//生成dfs编号
		try {
			File file = new File(path);  
			if(!file.getParentFile().exists()){  
				file.getParentFile().mkdirs();
			}  
			if(!file.exists()){  
				file.createNewFile();
			}  
			//写入txt文件    
			FileWriter fileWriter = new FileWriter(path,false);  
			BufferedWriter bw = new BufferedWriter(fileWriter);  
			
			bw.write("digraph G {\n");
			bw.write("\trankdir = " + direction + ";\n");
			bw.write("\tnode [fontsize=\"9\", shape = " + shape + "]\n");
			bw.write("\tedge [fontsize=\"9\"]\n\n");
			for(int i=0;i<roots.size();i++)
				genAbnormalSubTree(roots.get(i),bw,dr);
			bw.write("}");
			fileWriter.flush();  
			bw.close();  
			fileWriter.close();
		} catch (Exception e) {  
			return false;
		} 
		return true;
	}
	private void genAbnormalSubTree(Report rep, BufferedWriter bw,DiagnosisResult dr) throws IOException
	{
		bw.write("\t");
		String d = rep.get("delay").get(0);
		bw.write("\"node_"+rep.get("index").get(0)+"\"");
		String nodeColor = "black";
		ArrayList<Integer> abnormalOps=dr.getAbnormalOperations();
		int op=-1;
		try{op=Integer.parseInt(rep.get("dfsorder").get(0));}catch(Exception e){}
		if(abnormalOps.contains(op))
			nodeColor="red";
		bw.write(" [color=\""+nodeColor+"\" label=\""+rep.get("opname").get(0)+"\\n"+rep.get("agent").get(0)+
				"\\n"+rep.get("hostname").get(0)+"\\n"+String.format("%.2f", Double.parseDouble(d))+"ms");
		if(op>=0 && op<reports.size() && nodeColor.equals("red"))
			bw.write("("+String.format("%.2f", dr.getMinDelay(op))+","+String.format("%.2f", dr.getMaxDelay(op))+")");
		bw.write("\"]\n");
		List<String> children = rep.get("children");
		if(children == null) return;
		for(int j=0; j<children.size(); j++)
		{
			int index = Integer.parseInt(children.get(j));
			genAbnormalSubTree(reports.get(index),bw,dr);
		}
		for(int j=0; j<children.size(); j++)
		{
			String edgeColor = "black";
			bw.write("\t\t");
			String son = children.get(j);
			int index = Integer.parseInt(children.get(j));
			Report sonRep = reports.get(index);
			int sonOp = -1;
			try{sonOp=Integer.parseInt(sonRep.get("dfsorder").get(0));}catch(Exception e){}
			if(nodeColor.equals("red") && abnormalOps.contains(sonOp))
				edgeColor = "red";
			bw.write("\"node_"+rep.get("index").get(0)+"\"->\"node_"+son+"\" [color=\""+edgeColor+"\"]\n");
		}
	}


	private int order=0;
	public void depthFirstSearchOrder()//按深度优先的顺序给report设置编号（dfsorder），并设置深度depth
	{
		order = 0;
		for(int i=0; i<roots.size();i++)
			dfs(roots.get(i),0);
	}
	private void dfs(Report node, int depth)
	{
		node.put("depth",String.valueOf(depth));
		node.put("dfsorder",String.valueOf(order));
		order++;
		List<String> children = node.get("children");
		if(children==null)
			return;
		for(int i=0;i<children.size();i++)
		{
			Report rep = reports.get(Integer.parseInt(children.get(i)));
			dfs(rep,depth+1);
		}
	}
}












