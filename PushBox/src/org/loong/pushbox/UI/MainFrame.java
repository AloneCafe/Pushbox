package org.loong.pushbox.UI;

import java.awt.*;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.util.Stack;

import javax.swing.*;

import org.loong.pushbox.Audio;


public class MainFrame extends Frame implements KeyListener, WindowListener, ActionListener{
	
	//JLabel�ؼ�����ң���ͼ�ؿ�ָʾ��������ָʾ����
	private JLabel label_soldier;
	private JLabel label_maps;
	private JLabel label_steps;
	
	//�ڲ�ջ�����ÿһ����ǰ�ĵ�ͼ״̬�����ڵ�ͼ���ݻ�ԭ
	private Stack<int[][]> path = new Stack<int[][]>();
	
	//JButton�ؼ����ڲ���ť�����水ť��ѡ�ذ�ť����һ�ذ�ť����һ�ذ�ť��
	private JButton button_undo;
	private JButton button_retry;
	private JButton button_choose;
	private JButton button_back;
	private JButton button_next;
	
	//���x��y���������е�����λ��
	private int soldier_x;
	private int soldier_y;
	
	//��ǰ�ĵ�ͼ�ؿ�������ǰ�Ĳ���
	private int current_maps;
	private int current_steps;
	
	//��ά���飬��ŵ�ǰ�ؿ��ĵ�ͼ����
	private int map[][] = new int[12][16];
	
	//Audio�������ڲ�����Ч
	Audio audio;
	
	//�Ƿ��������ƶ������е��״λڲ�����
	boolean first_undo_time;
	
	public MainFrame(){
		//��ʼ����ͼ
		mapInit(0);
		//��ʼ���˵���
		menubarInit();
		//��ʼ������
		backgroundInit();
		//��ʼ��������
		setMainFrameUI();
		//��Ӵ����¼�������
		this.addWindowListener(this);
		//��Ӽ����¼�������
		this.addKeyListener(this);
		
		//��ʼ����Ч
		try {
			audio = new Audio();
		}
		catch(MalformedURLException e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	private void mapInit(int maps) {
		//���Ƶ�ͼ����
		current_maps = maps;
		//���ò���
		current_steps = 0;
		//forѭ�����ӵ�ͼ����ģ���ж�ȡ��ͼ���ݵ���ǰ�Ĺؿ���ͼ���ݶ�ά����
		for(int i = 0; i < map.length; i++)
		{
			for(int j = 0; j < map[i].length; j++)
			{
				map[i][j] = MapModel.map_model[current_maps][i][j];
			}
		}
		
		//����ͼ����Դ
		Icon icon_barrier = new ImageIcon("barrier.png");
		Icon icon_box = new ImageIcon("box.png");
		Icon icon_flag_uncomplete = new ImageIcon("flag_uncomplete.png");
		
		//��γ�ʼ�����Է��㲼��
		//��ʼ��������
		for(int i = 0; i < map.length; i++)
		{
			for(int j = 0; j < map[i].length; j++)
			{
				if(map[i][j] == 4)
				{
					Icon soldier = new ImageIcon("down.png");
					this.label_soldier = new JLabel(soldier);
					this.label_soldier.setBounds(50 * j, 29 + 50 * i, 50, 50);
					//�趨����ڶ�ά�����е�λ��
					this.soldier_x = i;
					this.soldier_y = j;
					this.add(this.label_soldier);
				}
			}
		}
		
		//��ʼ������
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
		
		//��ʼ��Ŀ��
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
		
		//��ʼ���ϰ���
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
		//ʤ���ж������ؿ���ͼ��û�����ӣ�����ֵ2����ʱ�򣬷���true����ʾʤ�������򷵻�false
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
		
		//��ΪpushMap�����������ƶ������״λڲ�ֵ����Ϊ�״�״̬
		first_undo_time = true;
		
		path.push(map);
	}
	
	//��ʼ���˵���
	private void menubarInit()
	{
		//��ʼ���ڲ���ť
		Icon undo = new ImageIcon("button_undo.png");
		button_undo = new JButton(undo);
		button_undo.setBounds(0, 629, 70, 30);
		button_undo.addActionListener(this);
		this.add(button_undo);
		
		//��ʼ�����水ť
		Icon retry = new ImageIcon("button_retry.png");
		button_retry = new JButton(retry);
		button_retry.setBounds(80, 629, 70, 30);
		button_retry.addActionListener(this);
		this.add(button_retry);
		
		//��ʼ��ѡ�ذ�ť
		Icon choose = new ImageIcon("button_choose.png");
		button_choose = new JButton(choose);
		button_choose.setBounds(160, 629, 70, 30);
		button_choose.addActionListener(this);
		this.add(button_choose);
		
		//��ʼ����һ�ذ�ť
		Icon back = new ImageIcon("button_back.png");
		button_back = new JButton(back);
		button_back.setBounds(240, 629, 70, 30);
		button_back.addActionListener(this);
		this.add(button_back);
		
		//��ʼ����һ�ذ�ť
		Icon next = new ImageIcon("button_next.png");
		button_next = new JButton(next);
		button_next.setBounds(320, 629, 70, 30);
		button_next.addActionListener(this);
		this.add(button_next);
		
		//��ʼ���ؿ���ͼָʾ��
		label_maps = new JLabel("��ǰ�ؿ���" + (current_maps + 1));
		label_maps.setBounds(400, 629, 110, 30);
		label_maps.setForeground(Color.WHITE);
		this.add(label_maps);
		
		//��ʼ������ָʾ��
		label_steps = new JLabel("������" + current_steps);
		label_steps.setBounds(520, 629, 110, 30);
		label_steps.setForeground(Color.YELLOW);
		this.add(label_steps);
	}
	
	//��ʼ��ʤ��
	private void winnerInit() {
		//ж�ؼ����¼�����������ֹ���ⰴ��
		this.removeKeyListener(this);
		//��ʼ��ʤ������
		Icon winner = new ImageIcon("winner.png");
		JLabel label_winner = new JLabel(winner);
		label_winner.setBounds(0, 29, 800, 600);
		this.add(label_winner);
		//��ʼ���˵���
		menubarInit();
		//��������
		audio.playWinner();
		
	}
	
	//��ʼ���ؿ���ͼ����
	private void backgroundInit() {
		//��ʼ���ؿ���ͼ����
		Icon bg = new ImageIcon("background.png");
		JLabel label_bg = new JLabel(bg);
		label_bg.setBounds(0, 29, 800, 600);
		this.add(label_bg);
	}

	//��ʼ�����������
	private void setMainFrameUI() {
		this.setLayout(null);
		this.setBackground(Color.BLACK);
		this.setTitle("��سɺ������");
		this.setLocation(100, 100);
		this.setSize(800, 629 + 30);
		this.setVisible(true);
		this.setResizable(false);
	}

	//������ж�
	@Override
	public void keyReleased(KeyEvent e) {
		int x, y;
		//��ȡ���JLabel�ؼ�������λ��
		x = (int)label_soldier.getLocation().getX();
		y = (int)label_soldier.getLocation().getY();
		//����JLabel�ؼ�
		JLabel box;
		
		//switch�ж�����ֵ
		switch(e.getKeyCode())
		{
			//�����ϼ�
			case KeyEvent.VK_UP:
				//�趨����ͼƬ
				label_soldier.setIcon(new ImageIcon("up.png"));
				
				//�����һλ���ǿյػ���Ŀ�������
				if(map[soldier_x - 1][soldier_y] == 0 || map[soldier_x - 1][soldier_y] == 3)
				{
					//����ʼ�ĵ�ͼ״̬ѹ��ջ
					pushMap(map);
					label_soldier.setLocation(x, y - 50);
					map[soldier_x][soldier_y] -= 4;
					map[soldier_x - 1][soldier_y] += 4;
					soldier_x--;
					current_steps++;
					//��������
					audio.playStep();
					break;
				}
				
				//������ϰ�����ִ�в���
				if(map[soldier_x - 1][soldier_y] == 1)
				{
					break;
				}
				
				//�����һλ���Ǻ��ӻ��ߺ�����Ŀ���������غϣ�������һ��λ�õ���һ��λ���ǿյػ��ߺ�����Ŀ���������غ�
				if((map[soldier_x - 1][soldier_y] == 5 || map[soldier_x - 1][soldier_y] == 2) && (map[soldier_x - 2][soldier_y] == 0 || map[soldier_x - 2][soldier_y] == 3))
				{
					//����ʼ�ĵ�ͼ״̬ѹ��ջ
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
					//��������
					audio.playStep();
					break;
				}
				
				break;
				
			//�����¼�
			case KeyEvent.VK_DOWN:
				//�趨����ͼƬ
				label_soldier.setIcon(new ImageIcon("down.png"));
				
				//�����һλ���ǿյػ���Ŀ�������
				if(map[soldier_x + 1][soldier_y] == 0 || map[soldier_x + 1][soldier_y] == 3)
				{
					//����ʼ�ĵ�ͼ״̬ѹ��ջ
					pushMap(map);
					label_soldier.setLocation(x, y + 50);
					map[soldier_x][soldier_y] -= 4;
					map[soldier_x + 1][soldier_y] += 4;
					soldier_x++;
					current_steps++;
					//��������
					audio.playStep();
					break;
				}
				
				//������ϰ�����ִ�в���
				if(map[soldier_x + 1][soldier_y] == 1)
				{
					break;
				}
				
				//�����һλ���Ǻ��ӻ��ߺ�����Ŀ���������غϣ�������һ��λ�õ���һ��λ���ǿյػ��ߺ�����Ŀ���������غ�
				if((map[soldier_x + 1][soldier_y] == 5 || map[soldier_x + 1][soldier_y] == 2) && (map[soldier_x + 2][soldier_y] == 0 || map[soldier_x + 2][soldier_y] == 3))
				{
					//����ʼ�ĵ�ͼ״̬ѹ��ջ
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
					//��������
					audio.playStep();
					break;
				}
				
				break;
				
			//�������
			case KeyEvent.VK_LEFT:
				//�趨����ͼƬ
				label_soldier.setIcon(new ImageIcon("left.png"));
				
				//�����һλ���ǿյػ���Ŀ�������
				if(map[soldier_x][soldier_y - 1] == 0 || map[soldier_x][soldier_y - 1] == 3)
				{
					//����ʼ�ĵ�ͼ״̬ѹ��ջ
					pushMap(map);
					label_soldier.setLocation(x - 50, y);
					map[soldier_x][soldier_y] -= 4;
					map[soldier_x][soldier_y - 1] += 4;
					soldier_y--;
					current_steps++;
					//��������
					audio.playStep();
					break;
				}
				
				//������ϰ�����ִ�в���
				if(map[soldier_x][soldier_y - 1] == 1)
				{
					break;
				}
				
				//�����һλ���Ǻ��ӻ��ߺ�����Ŀ���������غϣ�������һ��λ�õ���һ��λ���ǿյػ��ߺ�����Ŀ���������غ�
				if((map[soldier_x][soldier_y - 1] == 5 || map[soldier_x][soldier_y - 1] == 2) && (map[soldier_x][soldier_y - 2] == 0 || map[soldier_x][soldier_y - 2] == 3))
				{
					//����ʼ�ĵ�ͼ״̬ѹ��ջ
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
					//��������
					audio.playStep();
					break;
				}
				
				break;
			
				//�����Ҽ�
			case KeyEvent.VK_RIGHT:
				//�趨����ͼƬ
				label_soldier.setIcon(new ImageIcon("right.png"));
				
				//�����һλ���ǿյػ���Ŀ�������
				if(map[soldier_x][soldier_y + 1] == 0 || map[soldier_x][soldier_y + 1] == 3)
				{
					//����ʼ�ĵ�ͼ״̬ѹ��ջ
					pushMap(map);
					label_soldier.setLocation(x + 50, y);
					map[soldier_x][soldier_y] -= 4;
					map[soldier_x][soldier_y + 1] += 4;
					soldier_y++;
					current_steps++;
					//��������
					audio.playStep();
					break;
				}
				
				//������ϰ�����ִ�в���
				if(map[soldier_x][soldier_y + 1] == 1)
				{
					break;
				}
				
				//�����һλ���Ǻ��ӻ��ߺ�����Ŀ���������غϣ�������һ��λ�õ���һ��λ���ǿյػ��ߺ�����Ŀ���������غ�
				if((map[soldier_x][soldier_y + 1] == 5 || map[soldier_x][soldier_y + 1] == 2) && (map[soldier_x][soldier_y + 2] == 0 || map[soldier_x][soldier_y + 2] == 3))
				{
					//����ʼ�ĵ�ͼ״̬ѹ��ջ
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
					//��������
					audio.playStep();
					break;
				}
				
				break;
		}
		
		//��ǰ�������µ�JLabel����ָʾ��
		label_steps.setText("������" + current_steps);
		
		//�ж��Ƿ�ʤ��
		if(winnerJudge() == true)
		{
			//ʤ�����Ƴ����пؼ������ػ�
			this.removeAll();
			this.repaint();
			//��ʼ��ʤ������
			winnerInit();
		}
		
	}

	//����ر�ȷ��
	@Override
	public void windowClosing(WindowEvent e) {
		//��������
		audio.playStep();
		//ȷ����Ϣ�Ի���
		Object[] options ={"�����һ�Ҫ��", "ֱ���˳�" };
		int choice = JOptionPane.showOptionDialog(this, "��ȷ��Ҫ������Ϸ��", "�����", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
		if(choice == 1)
		{
			System.exit(0);
		}
		
	}

	//��ť�¼�
	@Override
	public void actionPerformed(ActionEvent e) {
		//��������
		audio.playClick();
		//�жϰ�ť���ͣ���ִ�ж�Ӧ����
		if(e.getSource() == button_undo)
		{
			//ջ�գ��򷢳���ʾ���ǵ�0��
			if(path.isEmpty())
			{
				//��������
				audio.playMsg();
				JOptionPane.showMessageDialog(this, "�Ѿ��Ǵ�ͷ��ʼ��", "��ʾ", JOptionPane.OK_OPTION);
				this.setFocusable(true);
				this.requestFocus();
				return;
			}
			
			//�Ʋ����ݼ�
			current_steps--;
			
			//��һ���ĵ�ͼ����
			map = path.pop();
			
			//�Ƴ������¼����������Ƴ����д���ؼ������ػ�
			this.removeKeyListener(this);
			this.removeAll();
			this.repaint();
			
			//����ͼ����Դ
			Icon icon_barrier = new ImageIcon("barrier.png");
			Icon icon_box = new ImageIcon("box.png");
			Icon icon_flag_uncomplete = new ImageIcon("flag_uncomplete.png");
			Icon icon_flag_completed = new ImageIcon("flag_completed.png");
			
			//��γ�ʼ�����Է��㲼��
			//��ʼ��������
			for(int i = 0; i < map.length; i++)
			{
				for(int j = 0; j < map[i].length; j++)
				{
					if(map[i][j] == 4 || map[i][j] == 7)
					{
						Icon soldier = new ImageIcon("down.png");
						this.label_soldier = new JLabel(soldier);
						this.label_soldier.setBounds(50 * j, 29 + 50 * i, 50, 50);
						//�趨����ڶ�ά�����е�λ��
						this.soldier_x = i;
						this.soldier_y = j;
						this.add(this.label_soldier);
					}
				}
			}
			
			//��ʼ������
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
			
			//��ʼ��Ŀ��
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
			
			//��ʼ���ϰ���
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
			
			//��ʼ���˵���
			menubarInit();
			//��ʼ������
			backgroundInit();
			//��Ӽ����¼�����������ȡ����
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
			//�Ƴ������¼����������Ƴ����д���ؼ������ػ�
			this.removeKeyListener(this);
			this.removeAll();
			this.repaint();
			//��ջڲ�ջ
			path.clear();
			//��ʼ����ͼ
			mapInit(current_maps);
			//��ʼ���˵���
			menubarInit();
			//��ʼ������
			backgroundInit();
			//��Ӽ����¼�����������ȡ����
			this.addKeyListener(this);
			this.setFocusable(true);
			this.requestFocus();
			
		}else if(e.getSource() == button_choose)
		{
			try
			{
				//��������Ի��򣬵�ȡ���򷵻�
				String input;
				//��������
				audio.playMsg();
				if((input = JOptionPane.showInputDialog("������ؿ�����", current_maps + 1)) == null)
				{
					return;
				}
				
				//���û�������ַ�����ȡ������ֵ
				int choice = Integer.parseInt(input);
				//��֤�ؿ���ͼ��ŵ���Ч��
				if((choice - 1) > MapModel.map_model.length || (choice - 1) < 0)
				{
					//��������
					audio.playMsg();
					//��ֵ��Ч���쳣������Ϣ��ʾ
					JOptionPane.showMessageDialog(this, "������ 1 �� " + (MapModel.map_model.length) + "֮�������", "��ʾ", JOptionPane.OK_OPTION);
					this.requestFocus();
					return;
				}
				
				//�Ƴ������¼����������Ƴ����д���ؼ������ػ�
				this.removeKeyListener(this);
				this.removeAll();
				this.repaint();
				//��ջڲ�ջ
				path.clear();
				//��ʼ����ͼ
				mapInit(choice - 1);
				//��ʼ���˵���
				menubarInit();
				//��ʼ������
				backgroundInit();
				//��Ӽ����¼�����������ȡ����
				this.addKeyListener(this);
				this.setFocusable(true);
				this.requestFocus();
				
			}catch(NumberFormatException ex)
			{
				System.out.println(ex.getStackTrace());
				//��������
				audio.playMsg();
				//��ֵ��Ч���쳣������Ϣ��ʾ
				JOptionPane.showMessageDialog(this, "������ 1 �� " + (MapModel.map_model.length) + "֮�������", "��ʾ", JOptionPane.OK_OPTION);
				this.requestFocus();
			}
			
		}else if(e.getSource() == button_back)
		{
			//��һ���ж�
			if(current_maps <= 0)
			{
				//��������
				audio.playMsg();
				JOptionPane.showMessageDialog(this, "���ǵ�һ�أ�", "��ʾ", JOptionPane.OK_OPTION);
				this.setFocusable(true);
				this.requestFocus();
				return;
			}
			//�Ƴ������¼����������Ƴ����д���ؼ������ػ�
			this.removeKeyListener(this);
			this.removeAll();
			this.repaint();
			//��ջڲ�ջ
			path.clear();
			//��ʼ����ͼ
			mapInit(--current_maps);
			//��ʼ���˵���
			menubarInit();
			//��ʼ������
			backgroundInit();
			//��Ӽ����¼�����������ȡ����
			this.addKeyListener(this);
			this.setFocusable(true);
			this.requestFocus();
			
		}else if(e.getSource() == button_next)
		{
			//���һ���ж�
			if(current_maps >= MapModel.map_model.length - 1)
			{
				//��������
				audio.playMsg();
				JOptionPane.showMessageDialog(this, "�������һ�أ�", "��ʾ", JOptionPane.OK_OPTION);
				this.setFocusable(true);
				this.requestFocus();
				return;
			}
			//�Ƴ������¼����������Ƴ����д���ؼ������ػ�
			this.removeKeyListener(this);
			this.removeAll();
			this.repaint();
			//��ջڲ�ջ
			path.clear();
			//��ʼ����ͼ
			mapInit(++current_maps);
			//��ʼ���˵���
			menubarInit();
			//��ʼ������
			backgroundInit();
			//��Ӽ����¼�����������ȡ����
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

