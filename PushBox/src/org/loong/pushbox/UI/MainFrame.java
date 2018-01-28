package org.loong.pushbox.UI;

import java.awt.*;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.util.Stack;

import javax.swing.*;

import org.loong.pushbox.Audio;


public class MainFrame extends Frame implements KeyListener, WindowListener, ActionListener{
	
	//JLabel控件（玩家，地图关卡指示器，步数指示器）
	private JLabel label_soldier;
	private JLabel label_maps;
	private JLabel label_steps;
	
	//悔步栈，存放每一步当前的地图状态，用于地图数据还原
	private Stack<int[][]> path = new Stack<int[][]>();
	
	//JButton控件（悔步按钮，重玩按钮，选关按钮，上一关按钮，下一关按钮）
	private JButton button_undo;
	private JButton button_retry;
	private JButton button_choose;
	private JButton button_back;
	private JButton button_next;
	
	//玩家x和y，在数组中的坐标位置
	private int soldier_x;
	private int soldier_y;
	
	//当前的地图关卡数，当前的步数
	private int current_maps;
	private int current_steps;
	
	//二维数组，存放当前关卡的地图数据
	private int map[][] = new int[12][16];
	
	//Audio对象，用于播放音效
	Audio audio;
	
	//是否是正常移动过程中的首次悔步操作
	boolean first_undo_time;
	
	public MainFrame(){
		//初始化地图
		mapInit(0);
		//初始化菜单栏
		menubarInit();
		//初始化背景
		backgroundInit();
		//初始化主窗体
		setMainFrameUI();
		//添加窗口事件监听器
		this.addWindowListener(this);
		//添加键盘事件监听器
		this.addKeyListener(this);
		
		//初始化音效
		try {
			audio = new Audio();
		}
		catch(MalformedURLException e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	private void mapInit(int maps) {
		//复制地图数据
		current_maps = maps;
		//重置步数
		current_steps = 0;
		//for循环，从地图数据模版中读取地图数据到当前的关卡地图数据二维数组
		for(int i = 0; i < map.length; i++)
		{
			for(int j = 0; j < map[i].length; j++)
			{
				map[i][j] = MapModel.map_model[current_maps][i][j];
			}
		}
		
		//加载图标资源
		Icon icon_barrier = new ImageIcon("barrier.png");
		Icon icon_box = new ImageIcon("box.png");
		Icon icon_flag_uncomplete = new ImageIcon("flag_uncomplete.png");
		
		//逐次初始化，以方便布局
		//初始化求生者
		for(int i = 0; i < map.length; i++)
		{
			for(int j = 0; j < map[i].length; j++)
			{
				if(map[i][j] == 4)
				{
					Icon soldier = new ImageIcon("down.png");
					this.label_soldier = new JLabel(soldier);
					this.label_soldier.setBounds(50 * j, 29 + 50 * i, 50, 50);
					//设定玩家在二维数组中的位置
					this.soldier_x = i;
					this.soldier_y = j;
					this.add(this.label_soldier);
				}
			}
		}
		
		//初始化盒子
		for(int i = 0; i < map.length; i++)
		{
			for(int j = 0; j < map[i].length; j++)
			{
				if(map[i][j] == 2)
				{
					JLabel label_box = new JLabel(icon_box);
					label_box.setBounds(50 * j, 29 + 50 * i, 50, 50);
					this.add(label_box);
				}
			}
		}
		
		//初始化目标
		for(int i = 0; i < map.length; i++)
		{
			for(int j = 0; j < map[i].length; j++)
			{
				if(map[i][j] == 3)
				{
					JLabel label_flag = new JLabel(icon_flag_uncomplete);
					label_flag.setBounds(50 * j, 29 + 50 * i, 50, 50);
					this.add(label_flag);
				}
			}	
		}
		
		//初始化障碍物
		for(int i = 0; i < map.length; i++)
		{
			for(int j = 0; j < map[i].length; j++)
			{
				if(map[i][j] == 1)
				{
					JLabel label_barrier = new JLabel(icon_barrier);
					label_barrier.setBounds(50 * j, 29 + 50 * i, 50, 50);
					this.add(label_barrier);
				}
			}	
		}
	}

	private boolean winnerJudge()
	{
		//胜利判定，当关卡地图上没有箱子（整数值2）的时候，返回true，表示胜利，否则返回false
		for(int i = 0; i < map.length; i++)
		{
			for(int j = 0; j < map[i].length; j++)
			{
				if(map[i][j] == 2)
				{
					return false;
				}
			}
		}
		
		return true;
	}
	private void pushMap(int[][] arg)
	{
		int[][] map = new int[arg.length][arg[0].length];
		for(int i = 0; i < arg.length; i++)
		{
			for(int j = 0; j < arg[i].length; j++)
			{
				map[i][j] = arg[i][j];
			}
		}
		
		//因为pushMap方法是正常移动，将首次悔步值重置为首次状态
		first_undo_time = true;
		
		path.push(map);
	}
	
	//初始化菜单栏
	private void menubarInit()
	{
		//初始化悔步按钮
		Icon undo = new ImageIcon("button_undo.png");
		button_undo = new JButton(undo);
		button_undo.setBounds(0, 629, 70, 30);
		button_undo.addActionListener(this);
		this.add(button_undo);
		
		//初始化重玩按钮
		Icon retry = new ImageIcon("button_retry.png");
		button_retry = new JButton(retry);
		button_retry.setBounds(80, 629, 70, 30);
		button_retry.addActionListener(this);
		this.add(button_retry);
		
		//初始化选关按钮
		Icon choose = new ImageIcon("button_choose.png");
		button_choose = new JButton(choose);
		button_choose.setBounds(160, 629, 70, 30);
		button_choose.addActionListener(this);
		this.add(button_choose);
		
		//初始化上一关按钮
		Icon back = new ImageIcon("button_back.png");
		button_back = new JButton(back);
		button_back.setBounds(240, 629, 70, 30);
		button_back.addActionListener(this);
		this.add(button_back);
		
		//初始化下一关按钮
		Icon next = new ImageIcon("button_next.png");
		button_next = new JButton(next);
		button_next.setBounds(320, 629, 70, 30);
		button_next.addActionListener(this);
		this.add(button_next);
		
		//初始化关卡地图指示器
		label_maps = new JLabel("当前关卡：" + (current_maps + 1));
		label_maps.setBounds(400, 629, 110, 30);
		label_maps.setForeground(Color.WHITE);
		this.add(label_maps);
		
		//初始化步数指示器
		label_steps = new JLabel("步数：" + current_steps);
		label_steps.setBounds(520, 629, 110, 30);
		label_steps.setForeground(Color.YELLOW);
		this.add(label_steps);
	}
	
	//初始化胜利
	private void winnerInit() {
		//卸载键盘事件监听器，防止额外按键
		this.removeKeyListener(this);
		//初始化胜利画面
		Icon winner = new ImageIcon("winner.png");
		JLabel label_winner = new JLabel(winner);
		label_winner.setBounds(0, 29, 800, 600);
		this.add(label_winner);
		//初始化菜单栏
		menubarInit();
		//播放声音
		audio.playWinner();
		
	}
	
	//初始化关卡地图背景
	private void backgroundInit() {
		//初始化关卡地图背景
		Icon bg = new ImageIcon("background.png");
		JLabel label_bg = new JLabel(bg);
		label_bg.setBounds(0, 29, 800, 600);
		this.add(label_bg);
	}

	//初始化主窗体参数
	private void setMainFrameUI() {
		this.setLayout(null);
		this.setBackground(Color.BLACK);
		this.setTitle("落地成盒清道夫");
		this.setLocation(100, 100);
		this.setSize(800, 629 + 30);
		this.setVisible(true);
		this.setResizable(false);
	}

	//方向键判定
	@Override
	public void keyReleased(KeyEvent e) {
		int x, y;
		//获取玩家JLabel控件的坐标位置
		x = (int)label_soldier.getLocation().getX();
		y = (int)label_soldier.getLocation().getY();
		//盒子JLabel控件
		JLabel box;
		
		//switch判定键码值
		switch(e.getKeyCode())
		{
			//方向上键
			case KeyEvent.VK_UP:
				//设定向上图片
				label_soldier.setIcon(new ImageIcon("up.png"));
				
				//如果下一位置是空地或者目标埋葬点
				if(map[soldier_x - 1][soldier_y] == 0 || map[soldier_x - 1][soldier_y] == 3)
				{
					//将初始的地图状态压入栈
					pushMap(map);
					label_soldier.setLocation(x, y - 50);
					map[soldier_x][soldier_y] -= 4;
					map[soldier_x - 1][soldier_y] += 4;
					soldier_x--;
					current_steps++;
					//播放声音
					audio.playStep();
					break;
				}
				
				//如果是障碍物则不执行操作
				if(map[soldier_x - 1][soldier_y] == 1)
				{
					break;
				}
				
				//如果下一位置是盒子或者盒子与目标埋葬点的重合，并且下一个位置的下一个位置是空地或者盒子与目标埋葬点的重合
				if((map[soldier_x - 1][soldier_y] == 5 || map[soldier_x - 1][soldier_y] == 2) && (map[soldier_x - 2][soldier_y] == 0 || map[soldier_x - 2][soldier_y] == 3))
				{
					//将初始的地图状态压入栈
					pushMap(map);
					box = (JLabel) this.getComponentAt(x, y - 50);
					box.setLocation(x, y - 100);
					label_soldier.setLocation(x, y - 50);
					y -= 50;
					
					map[soldier_x][soldier_y] -= 4;
					map[soldier_x - 1][soldier_y] += 2;
					map[soldier_x - 2][soldier_y] += 2;
					
					if(map[soldier_x - 2][soldier_y] == 2)
					{
						box.setIcon(new ImageIcon("box.png"));
					}
					else if(map[soldier_x - 2][soldier_y] == 5)
					{
						box.setIcon(new ImageIcon("flag_completed.png"));
					}
					
					soldier_x--;
					current_steps++;
					//播放声音
					audio.playStep();
					break;
				}
				
				break;
				
			//方向下键
			case KeyEvent.VK_DOWN:
				//设定向下图片
				label_soldier.setIcon(new ImageIcon("down.png"));
				
				//如果下一位置是空地或者目标埋葬点
				if(map[soldier_x + 1][soldier_y] == 0 || map[soldier_x + 1][soldier_y] == 3)
				{
					//将初始的地图状态压入栈
					pushMap(map);
					label_soldier.setLocation(x, y + 50);
					map[soldier_x][soldier_y] -= 4;
					map[soldier_x + 1][soldier_y] += 4;
					soldier_x++;
					current_steps++;
					//播放声音
					audio.playStep();
					break;
				}
				
				//如果是障碍物则不执行操作
				if(map[soldier_x + 1][soldier_y] == 1)
				{
					break;
				}
				
				//如果下一位置是盒子或者盒子与目标埋葬点的重合，并且下一个位置的下一个位置是空地或者盒子与目标埋葬点的重合
				if((map[soldier_x + 1][soldier_y] == 5 || map[soldier_x + 1][soldier_y] == 2) && (map[soldier_x + 2][soldier_y] == 0 || map[soldier_x + 2][soldier_y] == 3))
				{
					//将初始的地图状态压入栈
					pushMap(map);
					box = (JLabel) this.getComponentAt(x, y + 50);
					box.setLocation(x, y + 100);
					label_soldier.setLocation(x, y + 50);
					y += 50;
					
					map[soldier_x][soldier_y] -= 4;
					map[soldier_x + 1][soldier_y] += 2;
					map[soldier_x + 2][soldier_y] += 2;
					
					if(map[soldier_x + 2][soldier_y] == 2)
					{
						box.setIcon(new ImageIcon("box.png"));
					}else if(map[soldier_x + 2][soldier_y] == 5)
					{
						box.setIcon(new ImageIcon("flag_completed.png"));
					}
					
					soldier_x++;
					current_steps++;
					//播放声音
					audio.playStep();
					break;
				}
				
				break;
				
			//方向左键
			case KeyEvent.VK_LEFT:
				//设定向左图片
				label_soldier.setIcon(new ImageIcon("left.png"));
				
				//如果下一位置是空地或者目标埋葬点
				if(map[soldier_x][soldier_y - 1] == 0 || map[soldier_x][soldier_y - 1] == 3)
				{
					//将初始的地图状态压入栈
					pushMap(map);
					label_soldier.setLocation(x - 50, y);
					map[soldier_x][soldier_y] -= 4;
					map[soldier_x][soldier_y - 1] += 4;
					soldier_y--;
					current_steps++;
					//播放声音
					audio.playStep();
					break;
				}
				
				//如果是障碍物则不执行操作
				if(map[soldier_x][soldier_y - 1] == 1)
				{
					break;
				}
				
				//如果下一位置是盒子或者盒子与目标埋葬点的重合，并且下一个位置的下一个位置是空地或者盒子与目标埋葬点的重合
				if((map[soldier_x][soldier_y - 1] == 5 || map[soldier_x][soldier_y - 1] == 2) && (map[soldier_x][soldier_y - 2] == 0 || map[soldier_x][soldier_y - 2] == 3))
				{
					//将初始的地图状态压入栈
					pushMap(map);
					box = (JLabel) this.getComponentAt(x - 50, y);
					box.setLocation(x - 100, y);
					label_soldier.setLocation(x - 50, y);
					x -= 50;
					
					map[soldier_x][soldier_y] -= 4;
					map[soldier_x][soldier_y - 1] += 2;
					map[soldier_x][soldier_y - 2] += 2;
					
					if(map[soldier_x][soldier_y - 2] == 2)
					{
						box.setIcon(new ImageIcon("box.png"));
					}else if(map[soldier_x][soldier_y - 2] == 5)
					{
						box.setIcon(new ImageIcon("flag_completed.png"));
					}
					
					soldier_y--;
					current_steps++;
					//播放声音
					audio.playStep();
					break;
				}
				
				break;
			
				//方向右键
			case KeyEvent.VK_RIGHT:
				//设定向右图片
				label_soldier.setIcon(new ImageIcon("right.png"));
				
				//如果下一位置是空地或者目标埋葬点
				if(map[soldier_x][soldier_y + 1] == 0 || map[soldier_x][soldier_y + 1] == 3)
				{
					//将初始的地图状态压入栈
					pushMap(map);
					label_soldier.setLocation(x + 50, y);
					map[soldier_x][soldier_y] -= 4;
					map[soldier_x][soldier_y + 1] += 4;
					soldier_y++;
					current_steps++;
					//播放声音
					audio.playStep();
					break;
				}
				
				//如果是障碍物则不执行操作
				if(map[soldier_x][soldier_y + 1] == 1)
				{
					break;
				}
				
				//如果下一位置是盒子或者盒子与目标埋葬点的重合，并且下一个位置的下一个位置是空地或者盒子与目标埋葬点的重合
				if((map[soldier_x][soldier_y + 1] == 5 || map[soldier_x][soldier_y + 1] == 2) && (map[soldier_x][soldier_y + 2] == 0 || map[soldier_x][soldier_y + 2] == 3))
				{
					//将初始的地图状态压入栈
					pushMap(map);
					box = (JLabel) this.getComponentAt(x + 50, y);
					box.setLocation(x + 100, y);
					label_soldier.setLocation(x + 50, y);
					x += 50;
					
					map[soldier_x][soldier_y] -= 4;
					map[soldier_x][soldier_y + 1] += 2;
					map[soldier_x][soldier_y + 2] += 2;
					
					if(map[soldier_x][soldier_y + 2] == 2)
					{
						box.setIcon(new ImageIcon("box.png"));
					}else if(map[soldier_x][soldier_y + 2] == 5)
					{
						box.setIcon(new ImageIcon("flag_completed.png"));
					}
					
					soldier_y++;
					current_steps++;
					//播放声音
					audio.playStep();
					break;
				}
				
				break;
		}
		
		//当前步数更新到JLabel步数指示器
		label_steps.setText("步数：" + current_steps);
		
		//判定是否胜利
		if(winnerJudge() == true)
		{
			//胜利则移除所有控件，并重画
			this.removeAll();
			this.repaint();
			//初始化胜利画面
			winnerInit();
		}
		
	}

	//窗体关闭确认
	@Override
	public void windowClosing(WindowEvent e) {
		//播放声音
		audio.playStep();
		//确认消息对话框
		Object[] options ={"不，我还要玩", "直接退出" };
		int choice = JOptionPane.showOptionDialog(this, "您确定要结束游戏吗？", "埋队友", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
		if(choice == 1)
		{
			System.exit(0);
		}
		
	}

	//按钮事件
	@Override
	public void actionPerformed(ActionEvent e) {
		//播放声音
		audio.playClick();
		//判断按钮类型，以执行对应操作
		if(e.getSource() == button_undo)
		{
			//栈空，则发出提示已是第0步
			if(path.isEmpty())
			{
				//播放声音
				audio.playMsg();
				JOptionPane.showMessageDialog(this, "已经是从头开始了", "提示", JOptionPane.OK_OPTION);
				this.setFocusable(true);
				this.requestFocus();
				return;
			}
			
			//计步器递减
			current_steps--;
			
			//上一步的地图数据
			map = path.pop();
			
			//移除键盘事件监听器，移除所有窗体控件，并重画
			this.removeKeyListener(this);
			this.removeAll();
			this.repaint();
			
			//加载图标资源
			Icon icon_barrier = new ImageIcon("barrier.png");
			Icon icon_box = new ImageIcon("box.png");
			Icon icon_flag_uncomplete = new ImageIcon("flag_uncomplete.png");
			Icon icon_flag_completed = new ImageIcon("flag_completed.png");
			
			//逐次初始化，以方便布局
			//初始化求生者
			for(int i = 0; i < map.length; i++)
			{
				for(int j = 0; j < map[i].length; j++)
				{
					if(map[i][j] == 4 || map[i][j] == 7)
					{
						Icon soldier = new ImageIcon("down.png");
						this.label_soldier = new JLabel(soldier);
						this.label_soldier.setBounds(50 * j, 29 + 50 * i, 50, 50);
						//设定玩家在二维数组中的位置
						this.soldier_x = i;
						this.soldier_y = j;
						this.add(this.label_soldier);
					}
				}
			}
			
			//初始化盒子
			for(int i = 0; i < map.length; i++)
			{
				for(int j = 0; j < map[i].length; j++)
				{
					if(map[i][j] == 2)
					{
						JLabel label_box = new JLabel(icon_box);
						label_box.setBounds(50 * j, 29 + 50 * i, 50, 50);
						this.add(label_box);
					}
					
					if(map[i][j] == 5)
					{
						JLabel label_flag = new JLabel(icon_flag_completed);
						label_flag.setBounds(50 * j, 29 + 50 * i, 50, 50);
						this.add(label_flag);
					}
				}
			}
			
			//初始化目标
			for(int i = 0; i < map.length; i++)
			{
				for(int j = 0; j < map[i].length; j++)
				{
					if(map[i][j] == 3 || map[i][j] == 7)
					{
						JLabel label_flag = new JLabel(icon_flag_uncomplete);
						label_flag.setBounds(50 * j, 29 + 50 * i, 50, 50);
						this.add(label_flag);
					}
				}	
			}
			
			//初始化障碍物
			for(int i = 0; i < map.length; i++)
			{
				for(int j = 0; j < map[i].length; j++)
				{
					if(map[i][j] == 1)
					{
						JLabel label_barrier = new JLabel(icon_barrier);
						label_barrier.setBounds(50 * j, 29 + 50 * i, 50, 50);
						this.add(label_barrier);
					}
				}	
			}
			
			//初始化菜单栏
			menubarInit();
			//初始化背景
			backgroundInit();
			//添加键盘事件监听器并获取焦点
			this.addKeyListener(this);
			this.setFocusable(true);
			this.requestFocus();
			
			for(int i = 0; i < map.length; i++)
			{
				for(int j = 0; j < map[i].length; j++)
				{
					System.out.print("|" + map[i][j]);
				}
				System.out.println();
			}
			
		}else if(e.getSource() == button_retry)
		{
			//移除键盘事件监听器，移除所有窗体控件，并重画
			this.removeKeyListener(this);
			this.removeAll();
			this.repaint();
			//清空悔步栈
			path.clear();
			//初始化地图
			mapInit(current_maps);
			//初始化菜单栏
			menubarInit();
			//初始化背景
			backgroundInit();
			//添加键盘事件监听器并获取焦点
			this.addKeyListener(this);
			this.setFocusable(true);
			this.requestFocus();
			
		}else if(e.getSource() == button_choose)
		{
			try
			{
				//弹出窗体对话框，点取消则返回
				String input;
				//播放声音
				audio.playMsg();
				if((input = JOptionPane.showInputDialog("请输入关卡数：", current_maps + 1)) == null)
				{
					return;
				}
				
				//从用户输入的字符串中取出整数值
				int choice = Integer.parseInt(input);
				//验证关卡地图标号的有效性
				if((choice - 1) > MapModel.map_model.length || (choice - 1) < 0)
				{
					//播放声音
					audio.playMsg();
					//数值无效的异常处理，消息提示
					JOptionPane.showMessageDialog(this, "请输入 1 ～ " + (MapModel.map_model.length) + "之间的整数", "提示", JOptionPane.OK_OPTION);
					this.requestFocus();
					return;
				}
				
				//移除键盘事件监听器，移除所有窗体控件，并重画
				this.removeKeyListener(this);
				this.removeAll();
				this.repaint();
				//清空悔步栈
				path.clear();
				//初始化地图
				mapInit(choice - 1);
				//初始化菜单栏
				menubarInit();
				//初始化背景
				backgroundInit();
				//添加键盘事件监听器并获取焦点
				this.addKeyListener(this);
				this.setFocusable(true);
				this.requestFocus();
				
			}catch(NumberFormatException ex)
			{
				System.out.println(ex.getStackTrace());
				//播放声音
				audio.playMsg();
				//数值无效的异常处理，消息提示
				JOptionPane.showMessageDialog(this, "请输入 1 ～ " + (MapModel.map_model.length) + "之间的整数", "提示", JOptionPane.OK_OPTION);
				this.requestFocus();
			}
			
		}else if(e.getSource() == button_back)
		{
			//第一关判定
			if(current_maps <= 0)
			{
				//播放声音
				audio.playMsg();
				JOptionPane.showMessageDialog(this, "已是第一关！", "提示", JOptionPane.OK_OPTION);
				this.setFocusable(true);
				this.requestFocus();
				return;
			}
			//移除键盘事件监听器，移除所有窗体控件，并重画
			this.removeKeyListener(this);
			this.removeAll();
			this.repaint();
			//清空悔步栈
			path.clear();
			//初始化地图
			mapInit(--current_maps);
			//初始化菜单栏
			menubarInit();
			//初始化背景
			backgroundInit();
			//添加键盘事件监听器并获取焦点
			this.addKeyListener(this);
			this.setFocusable(true);
			this.requestFocus();
			
		}else if(e.getSource() == button_next)
		{
			//最后一关判定
			if(current_maps >= MapModel.map_model.length - 1)
			{
				//播放声音
				audio.playMsg();
				JOptionPane.showMessageDialog(this, "已是最后一关！", "提示", JOptionPane.OK_OPTION);
				this.setFocusable(true);
				this.requestFocus();
				return;
			}
			//移除键盘事件监听器，移除所有窗体控件，并重画
			this.removeKeyListener(this);
			this.removeAll();
			this.repaint();
			//清空悔步栈
			path.clear();
			//初始化地图
			mapInit(++current_maps);
			//初始化菜单栏
			menubarInit();
			//初始化背景
			backgroundInit();
			//添加键盘事件监听器并获取焦点
			this.addKeyListener(this);
			this.setFocusable(true);
			this.requestFocus();
		}
	}



	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}

