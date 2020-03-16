package com.bbs.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.bbs.entity.User;
import com.bbs.service.UserService;
import com.bbs.service.impl.UserServiceImpl;

/**
 * Servlet implementation class UserServlet
 */
@WebServlet("/UserServlet")
public class UserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private UserService us = new UserServiceImpl();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 设置请求编码
		req.setCharacterEncoding("UTF-8");
		//获得参数数据ֵ
		String op=req.getParameter("op");
		if("add".equals(op)){
			//ִ增加用户的方法
			saveUser(req,resp);
		}else if("findEdit".equals(op)){
			findEdit(req,resp);
		}else if("index".equals(op)){
			findAll(req,resp);
		}else if("upload".equals(op)){
			uploadUser(req,resp);
		}else if("delAll".equals(op)){
			delAll(req,resp);
		}else if("delById".equals(op)){
			delById(req,resp);
		}else if("serach".equals(op)){
			sreach(req,resp);
		}else if("update".equals(op)){
			updateUser(req,resp);
		}else if("loginOut".equals(op)){
			loginOut(req,resp);
		}
	}
	private void loginOut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// 清除保存在session中的用户名
		req.getSession().removeAttribute("userId");
		resp.sendRedirect("login.html");
	}

	private void updateUser(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		PrintWriter out = resp.getWriter();
		String userEmail = req.getParameter("userEmail");
		String userId = req.getParameter("userId");
		String userpsw = req.getParameter("userpsw");
		String userSex = req.getParameter("userSex");
		User user = new User();
		user.setUserId(userId);
		user.setUserEmail(userEmail);
		// 对密码进行MD5加密处理
		userpsw = DigestUtils.md5Hex(userpsw);
		user.setUserpsw(userpsw);
		user.setUserSex(userSex);
		boolean isOk = us.update(user);
		if(isOk) {
			out.write("true");
		}else {
			out.write("false");
		}
		out.flush();
	}

	private void sreach(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		List<User> lists = new ArrayList<User>();
		try {
			String userId = req.getParameter("userid");
			if (userId != null) {
				lists = us.findById(userId);
			}else {
				lists = us.getUserList();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		req.getSession().setAttribute("lists", lists);
		req.getRequestDispatcher("server/member-list.jsp").forward(req, resp);
	}

	private void delById(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String userId = req.getParameter("userId");
		resp.setContentType("application/json;charset=UTF-8");
		PrintWriter out = resp.getWriter();
		boolean isOk = us.delById(userId);
		if(isOk) {
			out.write("{\"result\":\"true\"}");
		}else {
			out.write("{\"result\":\"false\"}");
		}
		out.flush();
	}

	private void delAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String userIds = req.getParameter("userIds");
		//["test001","test002"]
		PrintWriter out = resp.getWriter();
		boolean isOk = us.delAll(userIds);
		if(isOk) {
			out.write("true");
		}else {
			out.write("false");
		}
		out.flush();
	}
	private User user = new User();// 当文件上传成功后，给这个用户复制，然后在增加的时候使用这个对象
	private void uploadUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String userEmail = null;
		String userId = null;
		String userpsw = null;
		String userSex = null;
		String userPhoto = null;
		resp.setContentType("application/json;charset=UTF-8");
		PrintWriter out = resp.getWriter();
		//获得文件上传的路径
		String filePath=this.getServletContext().getRealPath("/static/file");
		 //获得文件上传的请求方式
		boolean isMultipart = ServletFileUpload.isMultipartContent(req); 
		if(isMultipart) { //判断是否是二进制形式的数据上传
			FileItemFactory fac=new DiskFileItemFactory(); //创建文件上传的工厂对象
			ServletFileUpload upload=new ServletFileUpload(fac); 
			//设置上传文件的最大为10M
			upload.setFileSizeMax(10*1024*1024); 
			try { 
				List<FileItem> items =upload.parseRequest(req); 
				//遍历获得的所有数据 
				Iterator<FileItem> it=items.iterator(); 
				while(it.hasNext()) { 
					//获得表单元素 
					FileItem item=it.next();
					//判斷是否是普通文件
					if(item.isFormField()) { 
						 String name=item.getFieldName();//得到普通表單的name值ֵ
						 switch(name) { 
						 	case "userEmail":
								 userEmail=item.getString("UTF-8"); 
								 break; 
						 	case "userId":
								 userId=item.getString("UTF-8"); 
								 break; 
						 	case "userpsw":
								 userpsw=item.getString("UTF-8");
								 break; 
						 	case "userSex":
								userSex=item.getString("UTF-8"); 
								break; 
						 } 
					}else { 
					  userPhoto=item.getName();
					  //实现文件的上传 
					  File saveFile=new File(filePath,userPhoto);
					  item.write(saveFile);
					  // 向客户端响应一个上传成功的标识值
					  out.write("{\"code\":0}");
					}
				} 
			} catch (FileUploadException e) {
				  out.write("{\"code\":1}");
				  e.printStackTrace(); 
			} catch (Exception e) { 
				  out.write("{\"code\":2}");
				  e.printStackTrace(); 
			} 
		}
		// 使用md5的加密方式对数据进行加密操作
		 userpsw = DigestUtils.md5Hex(userpsw);
		 // 將獲得的數據設置給用戶對象
		 user.setUserId(userId); 
		 user.setUserpsw(userpsw);
		 user.setUserEmail(userEmail); 
		 user.setUserSex(userSex); 
		 user.setUserPhoto(userPhoto);
		 out.flush();
		 //System.out.println("===>"+userEmail+","+userId+","+userpsw+","+userSex+","+userPhoto);
	}

	private void findAll(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		//创建保存数据的集合
		List<User> lists = null;
		try {
			lists = us.getUserList();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		req.getSession().setAttribute("lists", lists);
		req.getRequestDispatcher("server/index.jsp").forward(req, resp);
	}

	private void findEdit(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String userId = req.getParameter("userId");
		User user = us.findEdit(userId);
		if(user != null) {
			req.getSession().setAttribute("user", user);
			resp.sendRedirect("server/member-edit.jsp");
		}
	}

	private void saveUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 
		PrintWriter out = response.getWriter();
		// user 就是放到uploadUser方法前面的用户对象
		boolean isOk = us.save(user);
		if(isOk) {
			out.write("true");
		}else {
			out.write("false");
		}
		out.flush();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
