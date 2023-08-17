package com.example.exampleboard;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.http.HttpServletResponse;

public class AlertMessage {
	 public static void init(HttpServletResponse response) {
	        response.setContentType("text/html; charset=euc-kr");
	        response.setCharacterEncoding("euc-kr");
	    }
	 
	    public static void alert(HttpServletResponse response, String alertText) throws IOException {
	        init(response);
	        PrintWriter out = response.getWriter();
	        out.println("<script>alert('" + alertText + "');</script> ");
	        out.flush();
	    }
	 
	    public static void alertAndClose(HttpServletResponse response, String alertText)
	            throws IOException {
	        init(response);
	        PrintWriter out = response.getWriter();
	        out.println("<script>alert('" + alertText + "'); opener.parent.location.reload(); window.close();</script> ");
	        out.flush();
	    }
	    
	    public static void alertAndMove(HttpServletResponse response, String alertText, String nextPage)
	            throws IOException {
	        init(response);
	        PrintWriter out = response.getWriter();
	        out.println("<script>alert('" + alertText + "');  location.href='" + nextPage + "';</script> ");
	        out.flush();
	    }
	 
	    public static void alertAndBack(HttpServletResponse response, String alertText) throws IOException {
	        init(response);
	        PrintWriter out = response.getWriter();
	        out.println("<script>alert('" + alertText + "'); history.go(-1);</script>");
	        out.flush();
	    }
}
