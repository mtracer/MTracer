package edu.nudt.xtrace;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Set;
import java.util.Iterator;

public class TaskClassifier{
	private Report condition;
	private String title;
	private String basePath;
	private Connection conn;
	private int numTasks;//符合条件的task总数

	private Map<String, Integer> tasks;//<taskID, numReports>，用于存储查询到的task的id，numReports目前没什么用
	private Map<String, ArrayList<String>> topos;//<topoID, <topo>>用于存储不同类型的topo
	private Map<String, ArrayList<String>> results;//<topoID,taskID list>，最终结果，用来存储每一类topo对应的taskid列表
	public  Map<String, ArrayList<String>> getResults(){return results;}
	private BufferedReader topobr;//file topology reader;
	private BufferedReader clazbr;//file classify reader;
	private BufferedWriter topobw;//file topology writer;
	private FileWriter topofw;
	private BufferedWriter clazbw;//file topology writer;
	private FileWriter clazfw;
	
	public TaskClassifier(){}
	private boolean init(Connection conn, String basePath, Report condition)
	{
		//参数赋值
		this.conn = conn;
		this.basePath = basePath;
		this.condition = condition;
		numTasks = 0;
		tasks = new HashMap<String, Integer>();
		topos = new HashMap<String, ArrayList<String>>();
		results = new HashMap<String, ArrayList<String>>();
		title = condition.get("title")==null?"*":condition.get("title").get(0);
		if(title.indexOf(",")!=-1 || title.indexOf("*")!=-1)//title可能包含多个类型的task，不能处理
			return false;
		
		//建立文件夹和文件
		title = title.replaceAll("-","");//dot命令可能不支持空格
		title = title.replaceAll(" ","_");
		String topoPath = basePath+"/"+ title + "/topology";
		String clazPath = basePath+"/"+ title + "/classify";
		try {
			File file = new File(basePath+"/"+ title);  
			if(!file.exists()){  
				file.mkdirs();
			}
			File topologyFile = new File(topoPath);  
			File classifyFile = new File(clazPath);  
			if(!topologyFile.exists()){  
				topologyFile.createNewFile();
			}  
			if(!classifyFile.exists()){  
				classifyFile.createNewFile();
			}  			
		} catch (Exception e) {return false;} 

		//建立writer和reader
		try {
			topobr = new BufferedReader(new FileReader(topoPath));
	  		clazbr = new BufferedReader(new FileReader(clazPath));
			topofw = new FileWriter(topoPath,true);
			topobw = new BufferedWriter(topofw); 
			clazfw = new FileWriter(clazPath,true);
			clazbw = new BufferedWriter(clazfw);  
		} catch (Exception e) {return false;} 

		//查询数据库
		if(loadData()==false)
			return false;

		//没查询到东西
		if(numTasks == 0)
			return false;
	return true;
	}
	private boolean loadData()//
	{
		try{//load tasks from mysql
			MySQLAccessor accessor = new MySQLAccessor(conn);
			int offset = 0; int length = 50;
			List<TaskRecord> tmpTasks = accessor.getTasksBySearch(condition,new Report(), offset, length);
			while(tmpTasks.size()>0)
			{
				for(int i=0;i<tmpTasks.size();i++){
					tasks.put(tmpTasks.get(i).getTaskId(), tmpTasks.get(i).getNumReports());
					numTasks++;
				}
				offset = offset+length;
				tmpTasks = accessor.getTasksBySearch(condition,new Report(), offset, length);
			}
		}catch(Exception e){
			return false;
		}
		
		try{//load topo from file
			String line = topobr.readLine();
			while(line != null){
				while(!line.equals("topology-start")){//寻找开头处
					line = topobr.readLine();
					if(line == null)
						break;
				}
				if(line != null)
				{
					ArrayList<String> topo = new ArrayList<String>();
					line = topobr.readLine();//读topoid行
					if(line!=null){
						String topoid = line;
						while(!line.equals("topology-start"))//读数据行
						{
							line = topobr.readLine();
							if(line == null)
								break;
							int indexMaohao = line.indexOf(':');
							int indexDouhao = line.indexOf(',');
							if(indexMaohao == -1 || indexDouhao == -1)//无效行
								continue;
							else
								topo.add(line);
						}
						if(topo.size()>0)
						topos.put(topoid,topo);
					}
				}
			}
		}catch(Exception e){
			return false;
		}
		return true;
	}
	private boolean classify(String taskid){
		//构建树结构
		Trace trace = new Trace();
		trace.findTopology(conn, taskid, String.valueOf(Double.MAX_VALUE));
		trace.depthFirstSearchOrder();

		//获取树结构
		ArrayList<Report> tree = trace.getReports();
		if(tree == null)
			return false;
		Map<String, Boolean> curTopo = new HashMap<String, Boolean>();//<"dfsorder:opname,depth",if scanned>
		int numRep = tree.size();
		for(int i=0;i<numRep;i++)
		{
			Report rep = tree.get(i);
			String dfsorder = rep.get("dfsorder")==null?"*":rep.get("dfsorder").get(0);
			String opname = rep.get("opname")==null?"*":rep.get("opname").get(0);
			String depth = rep.get("depth")==null?"*":rep.get("depth").get(0);
			curTopo.put(dfsorder+":"+opname+","+depth, false);
		}
		
		Set<String> topoIDs = topos.keySet();
		String curTopoID = "-1";
		for (Iterator it = topoIDs.iterator(); it.hasNext();) {//对每一个已存在的拓扑结构
			String topoID = (String) it.next();
			ArrayList<String> topo = topos.get(topoID);
			
			if(topo.size()!=curTopo.size())//节点数目不相等，肯定不是一类的
				continue;

			Set<String> keys = curTopo.keySet();
			for (Iterator it2 = keys.iterator(); it2.hasNext();) {//重置标记
				String key2 = (String) it2.next();
				curTopo.put(key2, false);
			}
			
			for(int i=0;i<topo.size();i++){//比对
				String op = topo.get(i);
				if(curTopo.get(op) == null)//有行不匹配
					break;
				else if(curTopo.get(op) == true)//已经标记了
					break;
				else
					curTopo.put(op,true);
			}
			
			boolean isMatched = true;
			keys = curTopo.keySet();
			for (Iterator it2 = keys.iterator(); it2.hasNext();) {//计算是否匹配上
				String key2 = (String) it2.next();
				if(curTopo.get(key2) == false){
					isMatched = false;
					break;
				}
			}

			if(isMatched == true){//若匹配上了
				curTopoID = topoID;
				break;
			}
		}
		if(curTopoID.equals("-1")){//不存在该结构,写topo
			//计算新topoID
			int tmpID = topos.size()+1;
			boolean isok = true;
			do{
				isok = true;
				Set<String> ids = topos.keySet();
				for (Iterator it = ids.iterator(); it.hasNext();) {
					String id = (String) it.next();
					if(id.equals(String.valueOf(tmpID))){
						isok = false;
						tmpID++;
						break;
					}
				}
			}while(isok == false);
			curTopoID = String.valueOf(tmpID);

			//写入文件并更新topos
			ArrayList<String> tmpTopo = new ArrayList<String>();
			try{
				topobw.write("topology-start\n");
				topobw.write(curTopoID+"\n");
				Set<String> keys = curTopo.keySet();
				Iterator it;
				int count = 0;
				for (it = keys.iterator(); it.hasNext();) {
					String key = (String) it.next();
					topobw.write(key+"\n");
					count++;
					if(count == 100){//每100行写一次
						topofw.flush();
						count = 0;
					}
					tmpTopo.add(key);
				}
				topos.put(curTopoID,tmpTopo);
				topobw.write("\n");
				topofw.flush();
			}catch(Exception e){return false;}
		}
		
		//写最终结果
		ArrayList<String> tmpTasks = null;
		if(results.get(curTopoID)==null){
			tmpTasks = new ArrayList<String>();
		}else{
			tmpTasks = results.get(curTopoID);;
		}
		tmpTasks.add(taskid);
		results.put(curTopoID, tmpTasks);

		//写claz	
		try{
			clazbw.write(taskid+":"+curTopoID+"\n");
			clazfw.flush();
		}catch(Exception e){return false;}

		//生成dot文件，用于生成图片
		String dotPath = basePath+"/"+ title + "/type_"+curTopoID+".dot";
		File dotFile = new File(dotPath);
		if(!dotFile.exists())
			genDotFile(curTopoID, trace);
		return true;
	}
	private void close()
	{
		try{
			topobw.close();
			topofw.close();
			clazbw.close();
			clazfw.close();
			topobr.close();
			clazbr.close();
		}catch(Exception e)
		{}
	}
	public int classify(Connection conn, String basePath, Report condition)
	{
		if(init(conn, basePath, condition)==false){//初始化
			close();
			return 0;
		}

		//已经计算的不用再计算
		try{
			String line = null;
			while((line = clazbr.readLine()) != null){
				int index = line.indexOf(':');
				String topoid = line.substring(index+1,line.length());
				String taskid = line.substring(0,index);
				
				if(tasks.get(taskid)!=null){
					//写最终结果
					ArrayList<String> tmpTasks = null;
					if(results.get(topoid)==null){
						tmpTasks = new ArrayList<String>();
					}else{
						tmpTasks = results.get(topoid);;
					}
					tmpTasks.add(taskid);
					results.put(topoid, tmpTasks);

					tasks.remove(taskid);
				}
			}
		}catch(Exception e)
		{}
		
		//计算未计算过的task
		Set<String> keys = tasks.keySet();
		for (Iterator it = keys.iterator(); it.hasNext();) {
			String id = (String) it.next();
			if(classify(id) == false){
				numTasks--;
				continue;
			}
		}
		close();
		return numTasks;
	}
	
	private boolean genDotFile(String topoID, Trace trace)//生成dot文件，用于生成图片
	{
		try {
			String path = basePath+"/"+ title + "/type_"+topoID+".dot";
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
			bw.write("\trankdir = TB;\n");
			bw.write("\tnode [fontsize=\"9\", shape = ellipse]\n");
			bw.write("\tedge [fontsize=\"9\"]\n\n");
			for(int i=0;i<trace.getRoots().size();i++)
				genSubTree(trace.getRoots().get(i),bw,trace.getReports());
			bw.write("}");
			fileWriter.flush();  
			bw.close();  
			fileWriter.close();
		} catch (Exception e) {  
			return false;
		} 
		return true;
	}
	private void genSubTree(Report rep, BufferedWriter bw, ArrayList<Report> reports) throws IOException
	{
		bw.write("\t");
		bw.write("\""+rep.get("index").get(0)+"\"");
		bw.write(" [label=\""+rep.get("opname").get(0)+"\"]\n");
		List<String> children = rep.get("children");
		if(children == null) return;
		for(int j=0; j<children.size(); j++)
		{
			int index = Integer.parseInt(children.get(j));
			genSubTree(reports.get(index),bw, reports);
		}
		for(int j=0; j<children.size(); j++)
		{
			bw.write("\t\t");
			String son = children.get(j);
			bw.write("\""+rep.get("index").get(0)+"\"->\""+son+"\" [color=\"black\"]\n");
		}
		
	}
	public boolean genGraph()//生成每种类型的拓扑图
	{
		Set<String> topoIDs = topos.keySet();
		for (Iterator it = topoIDs.iterator(); it.hasNext();) {//对每一个拓扑结构
			String topoID = (String) it.next();

			//先看看是不是已经有了
			String dotPath = basePath+"/"+ title + "/type_"+topoID+".dot";
			String svgPath = basePath+"/"+ title + "/type_"+topoID+".svg";
			try {
				File file = new File(basePath+"/"+ title);  
				if(!file.exists()){  
					return false;
				}
				File svgFile = new File(svgPath);
				if(svgFile.exists())//如果图片存在则不要操作
					continue;
				File dotFile = new File(dotPath);
				if(!dotFile.exists()){//如果dot不存在则生成
					Trace trace = new Trace();
					trace.findTopology(conn, topos.get(topoID).get(0), String.valueOf(Double.MAX_VALUE));
					genDotFile(topoID, trace);
				}
				String cmd = "dot -Tsvg "+ dotPath + " -o " +  svgPath;
				Process process = Runtime.getRuntime().exec(cmd); 
				process.waitFor();
			} catch (Exception e) {continue;} 
		}
		return true;
		
	}
}












