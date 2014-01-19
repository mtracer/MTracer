<%@ page import="edu.nudt.xtrace.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.File" %>

<jsp:useBean id="table" class="edu.nudt.xtrace.DistributeTable" scope="session"/>

<%!
public String displayMatrix(double [][] m)
{
	if(m==null)
		return "m == null";
	int rowNum = m.length;
	if(rowNum == 0)
		return "rowNum == 0";
	int colNum = m[0].length;
	String out="";
	for(int i=0;i<rowNum;i++){
		for(int j=0;j<colNum;j++)
			out+=m[i][j]+"  ";
		out+="<p>";
	}
	return out;
}
%>
<%
double[][] m1 = {{2, -1},{1,0},{-3,4},};
double[][] m2 = {{1,-2,-5},{3,4,0}};
/*
//test unit matrix
double[][] I = matrix.unitMatrix(17);
out.print(displayMatrix(I));
*//*
//test transpose
double[][] m3 = matrix.transpose(m1);
out.print("m1:<p>"+displayMatrix(m1));
out.print("transpose(m1):<p>"+displayMatrix(m3));
*//*
//test addition
double[][] m4 = matrix.transpose(m2);
out.print("m1:<p>"+displayMatrix(m1));
out.print("m4:<p>"+displayMatrix(m4));
double[][] m5 = matrix.add(m1,m4);
out.print("m1+m4:<p>"+displayMatrix(m5));
*//*
//test minus
double[][] m5 = matrix.transpose(m2);
out.print("m1:<p>"+displayMatrix(m1));
out.print("m5:<p>"+displayMatrix(m5));
double[][] m6 = matrix.minus(m1,m5);
out.print("m1+m5:<p>"+displayMatrix(m6));
*//*
//test multiply
out.print("m1:<p>"+displayMatrix(m1));
double[][] m7 = matrix.multiply(m1,7);
out.print("m1*7:<p>"+displayMatrix(m7));
*//*
//test multiply
out.print("m1:<p>"+displayMatrix(m1));
out.print("m2:<p>"+displayMatrix(m2));
double[][] m8 = matrix.multiply(m1,m2);
out.print("m1+m2:<p>"+displayMatrix(m8));
*//*
//test lengthSquare
double[] v={1,2,3,4,5};
double l=matrix.lengthSquare(v);
out.print(l);
*//*
//test lengthSquare
out.print("m1:<p>"+displayMatrix(m1));
double[] v = matrix.lengthSquare(m1);
for(int i = 0;i<v.length;i++)
	out.print(v[i]+"  ");
*//*
//test rowsAsMatrix
out.print("m1:<p>"+displayMatrix(m1));
double[][] m9 = matrix.rowsAsMatrix(m1,0,1);
out.print("m9:<p>"+displayMatrix(m9));
*//*
//test colsAsMatrix
out.print("m2:<p>"+displayMatrix(m2));
double[][] m10 = matrix.colsAsMatrix(m2,0,1);
out.print("m10:<p>"+displayMatrix(m10));
*/

/*
//t distribute table
for(int i=1;i<=50;i++){
	out.print(i+":  "+table.t(0.25,i)+"  "+table.t(0.1,i)+"  "+table.t(0.05,i)+"  "+table.t(0.025,i)+"  "+table.t(0.01,i)+"  "+table.t(0.005,i));
	out.print("<p>");
}
*/
//chi distribute table
for(int i=1;i<=50;i++){
	out.print(i+":  "+table.X2(0.995,i)+"  "+table.X2(0.99,i)+"  "+table.X2(0.975,i)+"  "+table.X2(0.95,i)+"  "+table.X2(0.9,i)+"  "+table.X2(0.75,i));
	out.print(i+":  "+table.X2(0.25,i)+"  "+table.X2(0.1,i)+"  "+table.X2(0.05,i)+"  "+table.X2(0.025,i)+"  "+table.X2(0.01,i)+"  "+table.X2(0.005,i));
	out.print("<p>");
}
%>



























