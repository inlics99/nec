package com.bbs.dao.user.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.bbs.commons.DataUtils;
import com.bbs.entity.User;

public class UserdaoImpl implements com.bbs.dao.user.Userdao {
	private ResultSet rs = null;
	@Override
	public int Verification(String userId, String userPsw) {
		//创建要执行的SQL命令
		String sql="select count(1) from bbs_user where userID=? and usepwd=?";
		//创建传递的参数数组
		Object[] parms= {userId,userPsw};
		//调用工具类中的查询方法
		rs=DataUtils.queryAll(sql, parms);
		//处理结果集
		try {
			if(rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			DataUtils.closeAll(null, null, rs);
		}
		return 0;
	}

	@Override
	public int save(User user) {
		String sql = "INSERT INTO bbs_user(userID,usepwd,userEmail,userSex,userPhoto,userScore,userLevel,userCreateDate) VALUES(?,?,?,?,?,default,default,default)";
		Object[] params = {user.getUserId(),user.getUserpsw(),user.getUserEmail(),user.getUserSex(),user.getUserPhoto()};
		return DataUtils.executeUpdate(sql, params);
	}

	@Override
	public List<User> getUserList() throws ParseException {
		List<User> lists = new ArrayList<User>();
		try {
			String sql = "select * from bbs_user";
			rs = DataUtils.queryAll(sql, null);
			while (rs.next()) {
				User user = new User();
				user.setUserId(rs.getString("userId"));
				user.setUserpsw(rs.getString("usepwd"));
				user.setUserEmail(rs.getString("userEmail"));
				user.setUserSex(rs.getString("userSex"));
				user.setUserPhoto(rs.getString("userPhoto"));
				user.setUserScore(rs.getInt("userScore"));
				user.setUserLevel(rs.getInt("userLevel"));
				user.setLevelDown(rs.getDate("userLock"));
				user.setUserLock(rs.getDate("userLock"));
				user.setUserCreateDate(rs.getDate("userCreateDate"));
				lists.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			DataUtils.closeAll(null, null, rs);
		}
		return lists;
	}

	@Override
	public User findEdit(String userId) {
		User user = new User();
		try {
			String sql = "select * from bbs_user where userID=?";
			Object[] params = {userId};
			rs = DataUtils.queryAll(sql, params);
			if(rs.next()) {
				user.setUserId(rs.getString("userId"));
				user.setUserpsw(rs.getString("usepwd"));
				user.setUserEmail(rs.getString("userEmail"));
				user.setUserSex(rs.getString("userSex"));
				user.setUserPhoto(rs.getString("userPhoto"));
				user.setUserScore(rs.getInt("userScore"));
				user.setUserLevel(rs.getInt("userLevel"));
				user.setLevelDown(rs.getDate("userLock"));
				user.setUserLock(rs.getDate("userLock"));
				user.setUserCreateDate(rs.getDate("userCreateDate"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			DataUtils.closeAll(null, null, rs);
		}
		return user;
	}

	@Override
	public int delAll(String[] uids) {
		
		StringBuffer sql = new StringBuffer("delete from bbs_user where userId in(");
		// 根据参数数组的长度，拼接锁需要的?号个数
		for (int i = 0; i < uids.length; i++) {
			sql.append("?");
			if (i != uids.length-1) {
				sql.append(",");
			}
		}
		sql.append(")");
		// delete from bbs_user where userid in(test001,test002)
		return DataUtils.executeUpdate(sql.toString(), uids);
	}

	@Override
	public int delById(String userId) {
		String sql = "delete from bbs_user where userId=?";
		Object[] params = {userId};
		return DataUtils.executeUpdate(sql, params);
	}

	@Override
	public List<User> findById(String userId) {
		List<User> lists = new ArrayList<User>();
		try {
			String sql = "select * from bbs_user where userId like concat('%',?,'%')";
			Object[] params = {userId};
			rs = DataUtils.queryAll(sql, params);
			while (rs.next()) {
				User user = new User();
				user.setUserId(rs.getString("userId"));
				user.setUserpsw(rs.getString("usepwd"));
				user.setUserEmail(rs.getString("userEmail"));
				user.setUserSex(rs.getString("userSex"));
				user.setUserPhoto(rs.getString("userPhoto"));
				user.setUserScore(rs.getInt("userScore"));
				user.setUserLevel(rs.getInt("userLevel"));
				user.setLevelDown(rs.getDate("userLock"));
				user.setUserLock(rs.getDate("userLock"));
				user.setUserCreateDate(rs.getDate("userCreateDate"));
				lists.add(user);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			DataUtils.closeAll(null, null, rs);
		}
		return lists;
	}

	@Override
	public int update(User user) {
		String sql = "update bbs_user set userEmail=?,usepwd=?,userSex=? where userID=?";
		Object[] params = {user.getUserEmail(),user.getUserpsw(),user.getUserSex(),user.getUserId()};
		return DataUtils.executeUpdate(sql, params);
	}

}
