<HTML>
<%
{
String url = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getRequestURI()+"?";
int offset = 0;
int length = 25;
if(request.getQueryString() != null)
{
	Enumeration paras=request.getParameterNames();
	while(paras.hasMoreElements()){
		String key=(String)paras.nextElement();
		String value=request.getParameter(key);
		if(key.equals("length")){
			try{length = Integer.parseInt(value);}
			catch(Exception e){length =25;}
		}
		else if(key.equals("offset")){
			try{offset = Integer.parseInt(value);}
			catch(Exception e){offset = 0;}
		}
		else url += key + "=" + value + "&";
	}
}

int prev = offset - length;
int next = offset + length;
if(prev < 0)
	prev = 0;

String prevUrl = url+"length="+length+"&offset="+prev;
String nextUrl = url+"length="+length+"&offset="+next;
String dis = "<a href=\""+prevUrl+"\">[&lt;&lt; Previous "+length+"]</a>";
dis += "- Showing Tasks "+offset+" - "+(next-1)+" -";
dis += "<a href=\""+nextUrl+"\">[Next "+length+" &gt;&gt;]</a>"+"   |";
out.print(dis);
%>

Start with: <input type="text" name="offset" value= "<%= request.getParameter("offset")==null ? "0" : request.getParameter("offset")%>" size="3"/>
Results per page: <input type="text" name="length" value="<%= request.getParameter("length")==null ? "25" : request.getParameter("length")%>" size="3" />
<script language="javascript">
function onChange(){
	var off = document.getElementsByName("offset");
	var len = document.getElementsByName("length");
	window.location.href="<%= url %>"+"offset="+off[0].value+"&length="+len[0].value; 
}
</script>
<input type="button" value="Change" onClick = "onChange(); return false;"/>

<%}%>
</HTML>


