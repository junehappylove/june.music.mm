/**
 * 中科方德软件有限公司<br>
 * myplayer:com.Runner.java
 * 日期:2016年10月21日
 */
package com.judy;

import com.judy.momoplayer.player.ui.Main;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * 启动音乐播放器 <br>
 * 
 * @author 王俊伟 wjw.happy.love@163.com
 * @date 2016年10月21日 下午9:53:17
 */
public class Runner extends Application {
	@Override
	public void start(Stage primaryStage) {
		Button btn = new Button();
		btn.setText("Say 'Hello World'");
		btn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				System.out.println("Hello World!");
			}
		});

		StackPane root = new StackPane();
		root.getChildren().add(btn);

		Scene scene = new Scene(root, 300, 250);

		primaryStage.setTitle("Hello World!");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		//launch(args);
		Main.main(args);
	}
}
