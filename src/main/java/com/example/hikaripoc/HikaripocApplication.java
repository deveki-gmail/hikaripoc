package com.example.hikaripoc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@RestController
@SpringBootApplication
public class HikaripocApplication {

	public static void main(String[] args) {
		SpringApplication.run(HikaripocApplication.class, args);
	}
	
	static public List<Connection> connections = new ArrayList<>();
	
	@GetMapping("/start")
	public String start(){
		System.out.println("start request received.");
		DataSource ds = getDataSource();
		MyTask task = new MyTask(ds);
		
		Thread t = new Thread(task);
		t.start();
		System.out.println("thread started....");
		return "Started";
	}
	
	
	@Bean
	public DataSource getDataSource(){
		
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:oracle:thin:@localhost:1521:xe");
		config.setUsername("deveki");
		config.setPassword("deveki");
		config.setDriverClassName("oracle.jdbc.OracleDriver");
		config.setMaxLifetime(180000);
		//config.addDataSourceProperty("connectionTimeout", "60000");
		//config.addDataSourceProperty("idleTimeout", "150000");
		//config.addDataSourceProperty("maximumPoolSize", "20");
		//config.addDataSourceProperty("minimumIdle", "50");
		//config.addDataSourceProperty("poolname", "hikari");
		//config.addDataSourceProperty("connectionTestQuery", "select 1 from dual");
		
		config.setMinimumIdle(5);
		config.setMaximumPoolSize(20);
		config.setIdleTimeout(150000);
		config.setConnectionTimeout(60000);
		config.setConnectionTestQuery("select 1 from dual");
		config.setPoolName("Deveki-Ka-Hikari-Pool");
		config.setLeakDetectionThreshold(170000);
		HikariDataSource dataSource = new HikariDataSource(config);
		return dataSource;
	}
	
	
	
}

class MyTask implements Runnable{
	DataSource ds;
	
	public MyTask(DataSource ds){
		this.ds = ds;
	}
	private void executeQuery(){
		Connection con =  null;
		Statement stmt = null;
		ResultSet rs = null;
		try{
			con = ds.getConnection();
			
			//Thread.sleep(10000);
			
			
			stmt = con.createStatement();
			rs = stmt.executeQuery("select count(*) as records from EMPLOYEE");
			while(rs.next()){
				System.out.println("count = "+rs.getInt("records"));
			}
			System.out.println("counting done....");
		}catch(SQLException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			try {
				rs.close();
				stmt.close();
				// close the connection
				//con.close();
				System.out.println("rs and stmt closed.");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void run(){
		executeQuery();
	}
}