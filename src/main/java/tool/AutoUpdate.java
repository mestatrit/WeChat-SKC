package tool;

/**
 * Created with Intellij IDEA.
 * User: WuHaoLin
 * Date: 1/17/14
 * Time: 6:17 PM
 */

import notice.ManageNotice;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 自动更新
 */
public class AutoUpdate {
	/**
	 * 内容的自动更新的周期
	 */
	public static final long UpdatePeriod = 1000 * 3600;//1小时
	//TODO

	private static Timer timer = new Timer();

	private static TimerTask timerTask = new TimerTask() {
		@Override
		public void run() {
			//依次调用每一个方法
			System.out.println("==========开始自动更新扫描===========");
			ManageNotice.update();
			Tool.update();//更新系统当前时间
			System.out.println("==========自动更新扫描结束==========");
		}
	};




	/**
	 * 从UpdatePeriod秒后,每隔UpdatePeriod秒更新内容一次
	 */
	public static void start() {
		try {
			timer.schedule(timerTask, UpdatePeriod, UpdatePeriod);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void stop() {
		try {
			timer.cancel();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		start();
	}

}
