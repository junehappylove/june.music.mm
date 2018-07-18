/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.momoplayer.playlist;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.DropMode;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListCellRenderer;
import javax.swing.TransferHandler;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;

import com.judy.momoplayer.player.ui.PlayerUI;
import com.judy.momoplayer.tag.SongInfoDialog;
import com.judy.momoplayer.util.Config;
import com.judy.momoplayer.util.FileNameFilter;
import com.judy.momoplayer.util.MultiImageBorder;
import com.judy.momoplayer.util.Util;

/**
 *
 * @author judy
 */
public class PlayListUI extends JPanel implements ActionListener, MouseListener, MouseMotionListener {

	private static final long serialVersionUID = 20071214L;
	private static Logger log = Logger.getLogger(PlayListUI.class.getName());
	//@SuppressWarnings("unused")
	private PlayListItem currentItem = null;// 当前的列表项
	private PlayList currentPlayList;// 当前所使用的播放列表
	private PlayerUI player;// 播放器的主UI界面兼播放器
	private JList<Object> leftList, rightList;// 左边和右边的列表
	private JSplitPane split;// 一个分隔栏，左右列表的中间的分割栏
	private Config config;// 全局配置对象
	private final DataFlavor flavor = new DataFlavor(MyData.class, "内部数据");
	private Vector<PlayList> playlists;// 所有的播放列表
	private static final Color BG = new Color(6, 6, 6);
	private static final Color FORE = new Color(100, 100, 100);
	private static final Color HILIGHT = new Color(0, 244, 245);
	private int rightIndex = -1;// 右边选中的下标，用于内部拖放用的
	private int leftIndex = -1;// 左边选中的下标
	private int onIndex = -1;// 目前鼠标在哪个下标的上面,如果下一个一样的话,就不用再设tooltip了
	private boolean rightHasFocus;// 指示右边的列表该不该有焦点
	private List<PlayListItem> clip;// 复制在粘帖板里面的数据

	public PlayListUI() {
		super(new BorderLayout());
		clip = new ArrayList<PlayListItem>();
		this.setMinimumSize(new Dimension(285, 100));
		this.setPreferredSize(new Dimension(285, 155));
		this.setBackground(Config.getConfig().getPlaylistBackground1());
	}

	public void setPlayerUI(PlayerUI player) {
		this.player = player;
	}

	/**
	 * 设置当前正在播放的项
	 * 
	 * @param item PlayListItem对象
	 */
	public void setCurrentItem(PlayListItem item) {
		this.currentItem = item;
		rightList.setSelectedValue(item, true);
		rightList.clearSelection();
	}

	/**
	 * 获取当前正在播放的项
	 * @return PlayListItem对象
	 */
	public PlayListItem getCurrentItem(){
		return currentItem;
	}

	public void loadUI(Component parent, Config config) {
		this.config = config;
		playlists = config.getPlayLists();
		if (playlists.size() == 0) {
			currentPlayList = new BasicPlayList(config);
			currentPlayList.setName(Config.getResource("playlistL.PreNEW") + 1);
			playlists.add(currentPlayList);
		} else {
			// 找到关闭的时候使用的列表以及放的歌曲,如有必要,自动播放
			String current = config.getCurrentPlayListName();
			if (current != null) {
				boolean find = false;
				for (PlayList pl : playlists) {
					if (pl.getName().equals(current)) {
						currentPlayList = pl;
						find = true;
						break;
					}
				}
				if (find == false) {
					currentPlayList = playlists.get(0);
				}
			} else {
				currentPlayList = playlists.get(0);
			}
		}
		player.setPlayList(currentPlayList);
		MultiImageBorder border = new MultiImageBorder(parent, config);
		border.setCorner1(Util.getImage("playlist/corner1.png"));
		border.setCorner2(Util.getImage("playlist/corner2.png"));
		border.setCorner3(Util.getImage("playlist/corner3.png"));
		border.setCorner4(Util.getImage("playlist/corner4.png"));
		border.setTop(Util.getImage("playlist/top.png"));
		border.setBottom(Util.getImage("playlist/bottom.png"));
		border.setLeft(Util.getImage("playlist/left.png"));
		border.setRight(Util.getImage("playlist/right.png"));
		this.setBorder(border);
		this.addMouseListener(border);
		this.addMouseMotionListener(border);
		initUI();
	}

	private void initUI() {
		leftList = new JList<Object>();//左侧分类列表
		rightList = new JList<Object>();//右侧歌曲列表
		leftList.setBackground(BG);
		rightList.setBackground(BG);
		leftList.setListData(playlists);
		leftList.setCellRenderer(new LeftListCellRenderer());
		rightList.setListData(currentPlayList.getAllItems());
		rightList.setCellRenderer(new RightListCellRenderer());
		leftList.addMouseListener(this);
		rightList.addMouseListener(this);
		rightList.addMouseMotionListener(this);
		JScrollPane jsp1 = new JScrollPane(leftList);
		JScrollPane jsp2 = new JScrollPane(rightList);
		//无边框设置
		//jsp1.setBorder(new EmptyBorder(0, 0, 0, 0));
		//jsp2.setBorder(new EmptyBorder(0, 0, 0, 0));
		BasicScrollBarUI momo1 = new MOMOScrollBarUI();
		BasicScrollBarUI momo2 = new MOMOScrollBarUI();
		jsp1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jsp2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jsp1.getVerticalScrollBar().setUI(momo1);
		jsp2.getVerticalScrollBar().setUI(momo2);
		jsp2.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {

			public void adjustmentValueChanged(AdjustmentEvent e) {
				if (!config.getReadTagInfoStrategy().equals(Config.READ_WHEN_DISPLAY)) {
					return;
				}
				// 如果滚动条正在拖动的时候，则不读取任何信息
				if (e.getValueIsAdjusting()) {
					return;
				}
				int from = rightList.getFirstVisibleIndex();
				int to = rightList.getLastVisibleIndex();
				if (from == -1 || to == -1) {
					return;
				}
				for (int i = from; i <= to; i++) {
					currentPlayList.getItemAt(i).getTagInfo();
				}
			}
		});
		split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, jsp1, jsp2);
		//split.setOneTouchExpandable(true);//设置是否可以折叠
		//split.setDividerSize(5);
		split.setBorder(new EmptyBorder(0, 0, 0, 0));
		this.add(split);
		initDragList();
		split.setDividerLocation(60);
		// 添加键盘事件,以可以用键盘直接删除列表项
		rightList.addKeyListener(new KeyAdapter() {

			public void keyPressed(KeyEvent ke) {
				if (rightIndex != -1 && rightIndex < currentPlayList.getPlaylistSize()) {
					if (ke.getKeyCode() == KeyEvent.VK_DELETE) {
						currentPlayList.removeItemAt(rightIndex);
						rightList.setListData(currentPlayList.getAllItems());
						if (rightIndex > currentPlayList.getPlaylistSize() - 1) {
							rightIndex = 0;
						}
						if (currentPlayList.getPlaylistSize() == 0) {
							return;
						}
						rightList.setSelectedValue(currentPlayList.getItemAt(rightIndex), rightHasFocus);
					}
				}
			}
		});
	}

	/**
	 * 初始化拖动列表内部数据
	 */
	private void initDragList() {

		DragSource ds = DragSource.getDefaultDragSource();
		ds.createDefaultDragGestureRecognizer(rightList, DnDConstants.ACTION_COPY_OR_MOVE, new DragGestureListener() {

			public void dragGestureRecognized(DragGestureEvent dge) {
				dge.startDrag(DragSource.DefaultCopyDrop, new Transferable() {

					public DataFlavor[] getTransferDataFlavors() {
						return new DataFlavor[] { flavor };
					}

					public boolean isDataFlavorSupported(DataFlavor flavor) {
						return flavor.equals(flavor);
					}

					public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
						if (isDataFlavorSupported(flavor)) {
							if (rightIndex == -1) {
								return null;
							}
							MyData<PlayListItem> my = new MyData<PlayListItem>(rightIndex,
									currentPlayList.getItemAt(rightIndex));
							return my;
						} else {
							throw new UnsupportedFlavorException(flavor);
						}
					}
				});
			}
		});
		rightList.setTransferHandler(new TransferHandler() {

			private static final long serialVersionUID = 20071214L;

			@Override
			public boolean canImport(TransferSupport support) {
				if (!config.isCanDnD()) {
					return false;
				}
				String os = System.getProperty("os.name");
				if (os.startsWith("Windows")) {
					return (support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)
							|| support.isDataFlavorSupported(flavor));
				} else if (os.startsWith("Linux")) {
					return support.isDataFlavorSupported(DataFlavor.stringFlavor)
							|| support.isDataFlavorSupported(flavor);
				} else {
					return super.canImport(support);
				}

			}

			@Override
			public boolean importData(TransferSupport support) {
				try {
					if (!canImport(support)) {
						return false;
					}
					Object toSelect = null;
					int index = 0;
					try {
						JList.DropLocation location = (JList.DropLocation) support.getDropLocation();
						index = location.getIndex();
					} catch (Exception exe) {
						exe.printStackTrace();
					}
					if (index < 0) {
						log.log(Level.SEVERE, "拖放点的index出现了小于0的情况!!");
						index = 0;
					}
					Transferable trans = support.getTransferable();
					Object obj = null;
					if (trans.isDataFlavorSupported(flavor)) {
						obj = trans.getTransferData(flavor);
						if (obj == null) {
							return false;
						}
						@SuppressWarnings("unchecked")
						MyData<PlayListItem> my = (MyData<PlayListItem>) obj;
						currentPlayList.addItemAt(my.getData(), index);
						if (index < my.getOldIndex()) {
							currentPlayList.removeItemAt(my.getOldIndex() + 1);
						} else {
							currentPlayList.removeItemAt(my.getOldIndex());
						}
						toSelect = my.getData();
					} else if (trans.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
						obj = trans.getTransferData(DataFlavor.javaFileListFlavor);
						@SuppressWarnings("unchecked")
						List<File> s = (List<File>) obj;
						FileNameFilter ff = new FileNameFilter(Config.EXTS,
								Config.getResource("playlist.filechooser.name"), true);
						for (File f : s) {
							if (f.exists()) {
								toSelect = addFiles(f, ff, index);
							}
						}
					} else if (trans.isDataFlavorSupported(DataFlavor.stringFlavor)
							&& System.getProperty("os.name").startsWith("Linux")) {
						obj = trans.getTransferData(DataFlavor.stringFlavor);
						log.info("得到的内容是：" + obj);
						String[] ss = obj.toString().split("\r\n");
						FileNameFilter ff = new FileNameFilter(Config.EXTS,
								Config.getResource("playlist.filechooser.name"), true);
						for (String s : ss) {
							try {
								File f = new File(new URI(s));
								toSelect = addFiles(f, ff, index);
							} catch (Exception exe) {
								exe.printStackTrace();
							}
						}
					}
					rightList.setListData(currentPlayList.getAllItems());
					rightList.setSelectedValue(toSelect, true);
					return true;
				} catch (UnsupportedFlavorException ex) {
					Logger.getLogger(PlayListUI.class.getName()).log(Level.SEVERE, null, ex);
				} catch (IOException ex) {
					Logger.getLogger(PlayListUI.class.getName()).log(Level.SEVERE, null, ex);
				}
				return super.importData(support);
			}
		});
		rightList.setDropMode(DropMode.INSERT);
	}

	/**
	 * 用于遍历子文件夹而加的方法,此方法会递归调用,直到 最后一层为止
	 * 
	 * @param f
	 *            文件或文件夹
	 * @param ff
	 *            过滤器
	 * @param index
	 *            要加在什么地方
	 * @return 最后选中的文件
	 */
	private Object addFiles(File f, FileNameFilter ff, int index) {
		Object toSelect = null;
		if (f.exists()) {
			// 如果是文件，则检查其后缀名
			// 现在不用检查了,错了也不要紧
			if (f.isFile() && ff.accept(f)) {
				PlayListItem item = new PlayListItem(Util.getSongName(f), f.getPath(), -1, true);
				currentPlayList.addItemAt(item, index);
				toSelect = item;
				// 如果是目录，则遍历它下面的文件，不再往下层遍历了
			} else if (f.isDirectory()) {
				File[] fs = f.listFiles(ff);
				for (File file : fs) {
					toSelect = addFiles(file, ff, index);
				}
			}
		}
		return toSelect;
	}

	public void setPlaylist(PlayList playlist) {
		if (playlist == currentPlayList) {
			return;
		}
		rightHasFocus = false;
		this.currentPlayList = playlist;
		player.setPlayList(playlist);
		config.setCurrentPlayListName(playlist.getName());
		rightList.setListData(currentPlayList.getAllItems());
		rightList.setSelectedValue(player.getCurrentItem(), true);
	}

	public void actionPerformed(ActionEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
			if (e.getSource() == rightList) {
				if (rightIndex == -1) {
					return;
				}
				PlayListItem pl = currentPlayList.getItemAt(rightIndex);
				log.info("pl=" + pl);
				if (pl != null) {
					currentPlayList.setItemSelected(pl, rightList.getSelectedIndex());
					player.setPlayerState(PlayerUI.PLAY);
					player.setCurrentSong(pl);
				}
			} else if (e.getSource() == leftList) {
				if (leftIndex == -1) {
					return;
				}
				PlayList pl = playlists.get(leftIndex);
				String s = JOptionPane.showInputDialog(config.getPlWindow(),
						Config.getResource("playlist.rename.content"));
				if (s != null && !s.trim().equals("")) {
					pl.setName(s);
				}
				leftList.setListData(playlists);
				repaint();
			}
		}
	}

	public void mousePressed(MouseEvent e) {
		// 这个方法就是保证无论是左键还是右键
		// 点击都算选中,默认的JAVA实现不是这样的,所以要重写
		if (e.getSource() == rightList) {
			rightHasFocus = true;
			rightIndex = rightList.locationToIndex(e.getPoint());
			if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK) {
				rightList.addSelectionInterval(rightIndex, rightIndex);
			} else if ((e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) == InputEvent.SHIFT_DOWN_MASK) {
				rightList.setSelectionInterval(rightList.getAnchorSelectionIndex(), rightIndex);
			} else {
				if (e.getButton() == MouseEvent.BUTTON1) {
					rightList.setSelectedIndex(rightIndex);
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					if (!rightList.isSelectedIndex(rightIndex)) {
						rightList.setSelectedIndex(rightIndex);
					}
				}
			}
			rightList.repaint();
		} else if (e.getSource() == leftList) {
			// rightHasFocus = false;
			leftIndex = leftList.locationToIndex(e.getPoint());
			leftList.setSelectedIndex(leftIndex);
			if (leftIndex == -1) {
				return;
			}
			Object obj = playlists.get(leftIndex);
			if (obj != null) {
				this.setPlaylist((PlayList) obj);
			}
			leftList.repaint();
		}
	}

	public void mouseReleased(MouseEvent e) {
		// if (e.getSource() == rightList) {
		// rightHasFocus = true;
		// rightIndex = rightList.locationToIndex(e.getPoint());
		//// rightList.setSelectedIndex(rightIndex);
		// }
		// if (e.getButton() == MouseEvent.BUTTON1) {
		// if (e.getSource() == leftList) {
		//// Object obj = leftList.getSelectedValue();
		//// if (obj != null) {
		//// this.setPlaylist((PlayList) obj);
		//// }
		// }
		// }
		if (e.getButton() == MouseEvent.BUTTON3) {
			if (e.getSource() == rightList) {
				showRightMenu(e);
			} else if (e.getSource() == leftList) {
				showLeftMenu(e);
			}
		}
	}

	/**
	 * 显示左边的右键菜单
	 * 
	 * @param e
	 *            鼠标事件
	 */
	private void showLeftMenu(MouseEvent e) {
		JPopupMenu pop = new JPopupMenu();
		// 新建列表
		pop.add(Config.getResource("playlistL.newplaylist")).addActionListener(new

		ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				int i = playlists.size();
				String name = Config.getResource("playlistL.PreNEW") + (i + 1);
				BasicPlayList plist = new BasicPlayList(config);
				plist.setName(name);
				playlists.add(plist);
				setPlaylist(plist);
				leftList.setListData(playlists);
				leftList.setSelectedValue(plist, true);
				repaint();
				// SwingUtilities.updateComponentTreeUI(leftList);
			}
		});
		// 添加列表
		pop.add(Config.getResource("playlistL.addplaylist")).addActionListener(new

		ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				JFileChooser jf = Util.getFileChooser(
						new FileNameFilter("m3u,pls", Config.getResource("playlist.filechooser.name"), true),
						JFileChooser.FILES_ONLY);
				int i = jf.showOpenDialog(config.getPlWindow());
				if (i == JFileChooser.APPROVE_OPTION) {
					BasicPlayList bp = new BasicPlayList(config);
					boolean b = bp.load(jf.getSelectedFile().getPath());
					if (b) {
						playlists.add(bp);
						setPlaylist(bp);
						leftList.setListData(playlists);
						leftList.setSelectedValue(bp, true);
						repaint();
						// SwingUtilities.updateComponentTreeUI(leftList);
						// SwingUtilities.updateComponentTreeUI(rightList);
					}
				}
			}
		});
		// 保存列表
		JMenuItem save = new JMenuItem(Config.getResource("playlistL.saveplaylist"));
		save.setEnabled(leftIndex != -1);
		pop.add(save).addActionListener(new

		ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				PlayList pl = playlists.get(leftIndex);
				JFileChooser jf = Util.getFileChooser(
						new FileNameFilter("m3u", Config.getResource("playlist.filechooser.name"), true),
						JFileChooser.FILES_ONLY);
				jf.setSelectedFile(new File(pl.getName() + ".m3u"));
				int i = jf.showSaveDialog(config.getPlWindow());
				if (i == JFileChooser.APPROVE_OPTION) {
					PlayList bp = playlists.get(leftIndex);
					@SuppressWarnings("unused")
					boolean b = bp.save(jf.getSelectedFile().getPath());
				}
			}
		});
		// 删除列表
		JMenuItem delete = new JMenuItem(Config.getResource("playlistL.deleteplaylist"));
		delete.setEnabled(leftIndex != -1);
		pop.add(delete).addActionListener(new

		ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				playlists.remove(leftIndex);
				if (playlists.size() > 0) {
					if (leftIndex > playlists.size() - 1) {
						setPlaylist(playlists.get(0));
					} else {
						setPlaylist(playlists.get(leftIndex));
					}
				}
				leftList.setListData(playlists);
				repaint();
				// SwingUtilities.updateComponentTreeUI(leftList);
			}
		});
		pop.addSeparator();
		// 保存所有
		pop.add(Config.getResource("playlistL.saveall")).addActionListener(new

		ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				JFileChooser jf = Util.getFileChooser(
						new FileNameFilter("m3u", Config.getResource("playlist.filechooser.name"), true),
						JFileChooser.DIRECTORIES_ONLY);
				int i = jf.showSaveDialog(config.getPlWindow());
				if (i == JFileChooser.APPROVE_OPTION) {
					File f = jf.getSelectedFile();
					if (!f.exists()) {
						f.mkdirs();
					}
					String dir = f.getPath();
					for (PlayList pl : playlists) {
						pl.save(dir + File.separator + pl.getName() + ".m3u");
					}
				}

			}
		});
		pop.addSeparator();
		// 重命名列表
		JMenuItem rename = new JMenuItem(Config.getResource("playlistL.rename"));
		rename.setEnabled(leftIndex != -1);
		pop.add(rename).addActionListener(new

		ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				PlayList pl = playlists.get(leftIndex);
				String s = JOptionPane.showInputDialog(config.getPlWindow(),
						Config.getResource("playlist.rename.content"));
				if (s != null && !s.trim().equals("")) {
					pl.setName(s);
				}
				leftList.setListData(playlists);
				repaint();
				// SwingUtilities.updateComponentTreeUI(leftList);
			}
		});
		pop.addSeparator();
		// 重排序列表
		pop.add(Config.getResource("playlistL.resort")).addActionListener(new

		ActionListener() {

			public

			void actionPerformed(ActionEvent ae) {
				Collections.sort(playlists, new Comparator<PlayList>() {

					public int compare(PlayList o1, PlayList o2) {
						String s1 = o1.getName();
						String s2 = o2.getName();
						return Collator.getInstance(Locale.CHINESE).compare(s1, s2);
					}
				});
				repaint();
				leftList.setListData(playlists);
				// SwingUtilities.updateComponentTreeUI(leftList);
			}
		});
		pop.show(e.getComponent(), e.getX(), e.getY());
	}

	/**
	 * 显示右边的右键菜单
	 * 
	 * @param e
	 *            鼠标事件
	 */
	private void showRightMenu(MouseEvent e) {
		JPopupMenu pop = new JPopupMenu();
		// 播放按钮
		if (rightIndex != -1) {
			pop.add("<html><b>" + Config.getResource("playlist.play")).addActionListener(new

			ActionListener() {

				public void actionPerformed(ActionEvent ae) {
					PlayListItem pl = currentPlayList.getItemAt(rightIndex);
					currentPlayList.setItemSelected(pl, rightList.getSelectedIndex());
					player.setPlayerState(PlayerUI.PLAY);
					player.setCurrentSong(pl);
					// player.play();
				}
			});
		}
		// pop.addSeparator();
		// 文件属性
		if (rightIndex != -1) {
			pop.add(Config.getResource("playlist.file.property")).addActionListener(new

			ActionListener() {

				public void actionPerformed(ActionEvent ae) {
					PlayListItem pl = currentPlayList.getItemAt(rightIndex);
					if (pl != null) {
						new SongInfoDialog(config.getPlWindow(), true, pl).setVisible(true);
					}
				}
			});
		}
		pop.addSeparator();
		// 添加菜单
		JMenu add = createAddMenu();
		pop.add(add);
		// pop.addSeparator();
		// 删除菜单
		JMenu delete = createDeleteMenu();
		pop.add(delete);
		// pop.addSeparator();
		// 重命名菜单
		JMenu rename = createRenameMenu();
		pop.add(rename);
		// pop.addSeparator();
		// 查找菜单
		JMenu search = createSearchMenu();
		pop.add(search);
		// pop.addSeparator();
		// 排序菜单
		JMenu sort = createSortMenu();
		pop.add(sort);
		// pop.addSeparator();
		// 编辑菜单
		JMenu edit = createEditMenu();
		pop.add(edit);
		// pop.addSeparator();
		// 模式菜单
		JMenu mode = createModeMenu();
		pop.add(mode);
		pop.show(e.getComponent(), e.getX(), e.getY());
		rightList.requestFocus();
	}

	/**
	 * 得到重命名的菜单,里面已经构造好了菜单项
	 * 
	 * @return 菜单
	 */
	private JMenu createRenameMenu() {
		JMenu menu = new JMenu(Config.getResource("playlist.rename"));
		if (rightIndex == -1) {
			menu.setEnabled(false);
			return menu;
		}
		// 加这个限制是因为只有文件才能重命名,而网上的文件就不行了
		menu.setEnabled(currentPlayList.getItemAt(rightIndex).isFile());
		// 歌曲.扩展名命名法
		menu.add(Config.getResource("playlist.rename.songName.ext")).addActionListener(new

		ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				PlayListItem item = currentPlayList.getItemAt(rightIndex);
				// 说明 这个时候正在播放这首歌曲,所以先停下来
				if (item == player.getCurrentItem()) {
					player.stop();
				}
				File file = new File(item.getLocation());
				File rename = new File(file.getParent(), item.getTitle() + "." + Util.getType(file));
				boolean b = file.renameTo(rename);
				if (b) {
					item.setLocation(rename.getPath());
				}
				log.log(Level.INFO, "把文件:" + file + "重命名为:" + rename);
				log.log(Level.INFO, "命名成功了吗?" + b);
			}
		});
		// 歌手 - 歌曲名.扩展名
		menu.add(Config.getResource("playlist.rename.artist.songName.ext")).addActionListener(new

		ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				PlayListItem item = currentPlayList.getItemAt(rightIndex);
				File file = new File(item.getLocation());
				File rename = new File(file.getParent(),
						item.getArtist() + " - " + item.getTitle() + "." + Util.getType(file));
				boolean b = file.renameTo(rename);
				if (b) {
					item.setLocation(rename.getPath());
				}
				log.log(Level.INFO, "把文件:" + file + "重命名为:" + rename);
				log.log(Level.INFO, "命名成功了吗?" + b);
			}
		});
		// 歌曲名 - 歌手.扩展名
		menu.add(Config.getResource("playlist.rename.songName.aritst.ext")).addActionListener(new

		ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				PlayListItem item = currentPlayList.getItemAt(rightIndex);
				File file = new File(item.getLocation());
				File rename = new File(file.getParent(),
						item.getTitle() + " - " + item.getArtist() + "." + Util.getType(file));
				boolean b = file.renameTo(rename);
				if (b) {
					item.setLocation(rename.getPath());
				}
				log.log(Level.INFO, "把文件:" + file + "重命名为:" + rename);
				log.log(Level.INFO, "命名成功了吗?" + b);
			}
		});
		return menu;
	}

	/**
	 * 得到添加菜单,里面已经构造好了子菜单项
	 * 
	 * @return 菜单
	 */
	private JMenu createAddMenu() {
		JMenu menu = new JMenu(Config.getResource("playlist.add"));
		// 添加文件
		menu.add(Config.getResource("playlist.add.file")).addActionListener(new

		ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				JFileChooser jf = Util.getFileChooser(
						new FileNameFilter(Config.EXTS, Config.getResource("playlist.filechooser.name"), true),
						JFileChooser.FILES_ONLY);
				int i = jf.showOpenDialog(config.getPlWindow());
				if (i == JFileChooser.APPROVE_OPTION) {
					File f = jf.getSelectedFile();
					PlayListItem item = new PlayListItem(Util.getSongName(f), f.getPath(), -1, true);
					if (rightIndex == -1) {
						currentPlayList.appendItem(item);
					} else {
						currentPlayList.addItemAt(item, rightIndex);
					}
					rightList.setListData(currentPlayList.getAllItems());
				}
			}
		});
		// 添加文件夹
		menu.add(Config.getResource("playlist.add.dir")).addActionListener(new

		ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				JFileChooser jf = Util.getFileChooser(
						new FileNameFilter(Config.EXTS, Config.getResource("playlist.filechooser.name"), true),
						JFileChooser.DIRECTORIES_ONLY);
				int i = jf.showOpenDialog(config.getPlWindow());
				if (i == JFileChooser.APPROVE_OPTION) {
					File f = jf.getSelectedFile();
					if (f.isDirectory()) {
						File[] fs = f.listFiles(new FileNameFilter(Config.EXTS,
								Config.getResource("playlist.filechooser.name"), false));
						for (File file : fs) {
							PlayListItem item = new PlayListItem(Util.getSongName(file), file.getPath(), -1, true);
							if (rightIndex == -1) {
								currentPlayList.appendItem(item);
							} else {
								currentPlayList.addItemAt(item, rightIndex);
							}
						}
						rightList.setListData(currentPlayList.getAllItems());
					}
				}
			}
		});
		// 添加网络地址
		menu.add(Config.getResource("playlist.add.url")).addActionListener(new

		ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				String s = JOptionPane.showInputDialog(config.getPlWindow(),
						Config.getResource("playlist.add.inputurl"));
				if (s != null) {
					if (Config.startWithProtocol(s)) {
						PlayListItem item = new PlayListItem(s, s, -1, false);
						if (rightIndex == -1) {
							currentPlayList.appendItem(item);
						} else {
							currentPlayList.addItemAt(item, rightIndex);
						}
						rightList.setListData(currentPlayList.getAllItems());
					} else {
						JOptionPane.showMessageDialog(config.getPlWindow(),
								Config.getResource("playlist.add.invalidUrl"));
					}
				}
			}
		});
		return menu;
	}

	/**
	 * 创建删除相关的菜单以及菜单项
	 * 
	 * @return 菜单
	 */
	private JMenu createDeleteMenu() {
		JMenu menu = new JMenu(Config.getResource("playlist.delete"));
		if (rightIndex == -1) {
			menu.setEnabled(false);
			return menu;
		}
		// 删除当前项
		menu.add(Config.getResource("playlist.delete.select")).addActionListener(new

		ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				@SuppressWarnings("deprecation")
				Object[] objs = rightList.getSelectedValues();
				for (Object obj : objs) {
					currentPlayList.removeItem((PlayListItem) obj);
				}
				rightList.setListData(currentPlayList.getAllItems());
				rightList.setSelectedIndex(rightIndex);
			}
		});
		// 删除重复项
		menu.add(Config.getResource("playlist.delete.repeat")).addActionListener(new

		ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				Vector<PlayListItem> vs = currentPlayList.getAllItems();
				for (int i = 0; i < vs.size() - 1; i++) {
					PlayListItem item1 = vs.get(i);
					for (int j = i + 1; j < vs.size(); j++) {
						PlayListItem item2 = vs.get(j);
						if (item1.getLocation().equals(item2.getLocation())) {
							vs.remove(item2);
							j--;
						}
					}
				}
				rightList.setListData(vs);
			}
		});
		// 删除错误文件
		menu.add(Config.getResource("playlist.delete.error")).addActionListener(new

		ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				Vector<PlayListItem> vs = currentPlayList.getAllItems();
				List<PlayListItem> temp = new ArrayList<PlayListItem>();
				for (PlayListItem item : vs) {
					// 如果是文件,则文件不存在或者文件格式不合法则为错误文件
					if (item.isFile) {
						File f = new File(item.getLocation());
						if (f.exists()) {
							if (item.getFormattedLength().equals("-1")) {
								temp.add(item);
							}
						} else {
							temp.add(item);
						}
					} else {// 如果是网络文件则无法判断了,留以后实现
					}
				}
				vs.removeAll(temp);
				rightList.setListData(vs);
			}
		});

		// 清空列表
		menu.add(Config.getResource("playlist.delete.all")).addActionListener(new

		ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				currentPlayList.removeAllItems();
				rightList.setListData(currentPlayList.getAllItems());
			}
		});
		// 物理删除
		JMenuItem delete = new JMenuItem(Config.getResource("playlist.delete.deletefile"));
		menu.add(delete);
		delete.setEnabled(!config.isDisableDelete());
		delete.addActionListener(new

		ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(config.getPlWindow(),
						Config.getResource("playlist.deletefile.confirm"), Config.getResource("confirm"),
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
					@SuppressWarnings("deprecation")
					Object[] objs = rightList.getSelectedValues();
					for (Object obj : objs) {
						PlayListItem pl = (PlayListItem) obj;
						if (pl.isFile()) {
							File f = new File(pl.getLocation());
							boolean b = f.delete();
							if (b) {
								currentPlayList.removeItem(pl);
								repaint();
							}
						}
					}
				}
			}
		});
		return menu;
	}

	private JMenu createSearchMenu() {
		// 先简单实现,有时间或者有机会的时候,
		// 可以再实现的完美一些,比如继续查找,混合查找等等
		JMenu menu = new JMenu(Config.getResource("playlist.search"));
		// 快速查找
		menu.add(Config.getResource("playlist.search.fileName")).addActionListener(new

		ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				String s = JOptionPane.showInputDialog(config.getPlWindow(),
						Config.getResource("playlist.search.inputFileName"));
				if (s != null) {
					s = s.trim();
					for (PlayListItem item : currentPlayList.getAllItems()) {
						String name = Util.getSongName(item.getLocation());
						if (name.contains(s)) {
							rightList.setSelectedValue(item, true);
						}
					}
				}
			}
		});
		menu.add(Config.getResource("playlist.search.title")).addActionListener(new

		ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				String s = JOptionPane.showInputDialog(config.getPlWindow(),
						Config.getResource("playlist.search.inputTitle"));
				if (s != null) {
					s = s.trim();
					for (PlayListItem item : currentPlayList.getAllItems()) {
						String name = item.getTitle();
						if (name.contains(s)) {
							rightList.setSelectedValue(item, true);
						}
					}
				}
			}
		});
		menu.add(Config.getResource("playlist.search.artist")).addActionListener(new

		ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				String s = JOptionPane.showInputDialog(config.getPlWindow(),
						Config.getResource("playlist.search.inputArtist"));
				if (s != null) {
					s = s.trim();
					for (PlayListItem item : currentPlayList.getAllItems()) {
						String name = item.getArtist();
						if (name.contains(s)) {
							rightList.setSelectedValue(item, true);
						}
					}
				}
			}
		});
		menu.add(Config.getResource("playlist.search.album")).addActionListener(new

		ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				String s = JOptionPane.showInputDialog(config.getPlWindow(),
						Config.getResource("playlist.search.inputAlbum"));
				if (s != null) {
					s = s.trim();
					for (PlayListItem item : currentPlayList.getAllItems()) {
						String name = item.getAlbum();
						if (name.contains(s)) {
							rightList.setSelectedValue(item, true);
						}
					}
				}
			}
		});
		return menu;
	}

	private JMenu createSortMenu() {
		JMenu menu = new JMenu(Config.getResource("playlist.sort"));
		// 按歌手
		menu.add(Config.getResource("playlist.sort.artist")).addActionListener(new

		ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				Collections.sort(currentPlayList.getAllItems(), new Comparator<PlayListItem>() {

					public int compare(PlayListItem o1, PlayListItem o2) {
						String s1 = o1.getArtist() == null ? "" : o1.getArtist();
						String s2 = o2.getArtist() == null ? "" : o2.getArtist();
						return Collator.getInstance(Locale.CHINESE).compare(s1, s2);
					}
				});
				rightList.setListData(currentPlayList.getAllItems());
			}
		});
		// 按标题
		menu.add(Config.getResource("playlist.sort.title")).addActionListener(new

		ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				Collections.sort(currentPlayList.getAllItems(), new Comparator<PlayListItem>() {

					public int compare(PlayListItem o1, PlayListItem o2) {
						String s1 = o1.getTitle() == null ? "" : o1.getTitle();
						String s2 = o2.getTitle() == null ? "" : o2.getTitle();
						return Collator.getInstance(Locale.CHINESE).compare(s1, s2);
					}
				});
				rightList.setListData(currentPlayList.getAllItems());
			}
		});
		// 按专辑
		menu.add(Config.getResource("playlist.sort.album")).addActionListener(new

		ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				Collections.sort(currentPlayList.getAllItems(), new Comparator<PlayListItem>() {

					public int compare(PlayListItem o1, PlayListItem o2) {
						String s1 = o1.getAlbum() == null ? "" : o1.getAlbum();
						String s2 = o2.getAlbum() == null ? "" : o2.getAlbum();
						return Collator.getInstance(Locale.CHINESE).compare(s1, s2);
					}
				});
				rightList.setListData(currentPlayList.getAllItems());
			}
		});
		// 按文件名
		menu.add(Config.getResource("playlist.sort.fileName")).addActionListener(new

		ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				Collections.sort(currentPlayList.getAllItems(), new Comparator<PlayListItem>() {

					public int compare(PlayListItem o1, PlayListItem o2) {
						String s1 = o1.getName() == null ? "" : o1.getName();
						String s2 = o2.getName() == null ? "" : o2.getName();
						return Collator.getInstance(Locale.CHINESE).compare(s1, s2);
					}
				});
				rightList.setListData(currentPlayList.getAllItems());
			}
		});
		// 按歌曲长度
		menu.add(Config.getResource("playlist.sort.length")).addActionListener(new

		ActionListener() {

			public

			void actionPerformed(ActionEvent ae) {
				Collections.sort(currentPlayList.getAllItems(), new Comparator<PlayListItem>() {

					public int compare(PlayListItem o1, PlayListItem o2) {
						return (int) (o1.getLength() - o2.getLength());
					}
				});
				rightList.setListData(currentPlayList.getAllItems());
			}
		});
		menu.addSeparator();
		// 随机乱序
		menu.add(Config.getResource("playlist.sort.random")).addActionListener(new

		ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				Collections.shuffle(currentPlayList.getAllItems());
				rightList.setListData(currentPlayList.getAllItems());
			}
		});
		return menu;
	}

	private JMenu createEditMenu() {
		JMenu menu = new JMenu(Config.getResource("playlist.edit"));
		// 剪切
		if (rightIndex != -1) {
			menu.add(Config.getResource("playlist.edit.cut")).addActionListener(new

			ActionListener() {

				public void actionPerformed(ActionEvent ae) {
					clip.clear();
					@SuppressWarnings("deprecation")
					Object[] objs = rightList.getSelectedValues();
					for (Object obj : objs) {
						PlayListItem item = (PlayListItem) obj;
						currentPlayList.removeItem(item);
						clip.add(item);
					}
					rightList.setListData(currentPlayList.getAllItems());
					rightList.setSelectedIndex(rightIndex);
				}
			});
		}
		// 复制
		if (rightIndex != -1) {
			menu.add(Config.getResource("playlist.edit.copy")).addActionListener(new

			ActionListener() {

				public void actionPerformed(ActionEvent ae) {
					clip.clear();
					@SuppressWarnings("deprecation")
					Object[] objs = rightList.getSelectedValues();
					for (Object obj : objs) {
						PlayListItem item = (PlayListItem) obj;
						clip.add(item);
					}
					rightList.setSelectedIndex(rightIndex);
				}
			});
		}
		// 粘帖
		menu.add(Config.getResource("playlist.edit.paste")).addActionListener(new

		ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				if (clip.size() > 0) {
					PlayListItem last = null;
					for (PlayListItem item : clip) {
						PlayListItem it = new PlayListItem(item.getName(), item.getLocation(), item.getLength(),
								item.isFile());
						int index = rightIndex;
						if (index == -1) {
							currentPlayList.appendItem(it);
						} else {
							currentPlayList.addItemAt(it, index);
						}
						last = it;
					}
					rightList.setListData(currentPlayList.getAllItems());
					rightList.setSelectedValue(last, true);
					rightList.requestFocus();
					rightHasFocus = true;
				}
			}
		});
		menu.addSeparator();
		// 全选
		menu.add(Config.getResource("playlist.edit.selectAll")).addActionListener(new

		ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				rightList.setSelectionInterval(0, currentPlayList.getPlaylistSize() - 1);
			}
		});
		// 全不选
		menu.add(Config.getResource("playlist.edit.selectNone")).addActionListener(new

		ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				rightList.clearSelection();
				rightIndex = -1;
			}
		});
		// 反选
		menu.add(Config.getResource("playlist.edit.selectReverse")).addActionListener(new

		ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				int[] indexes = rightList.getSelectedIndices();
				List<Integer> list = new ArrayList<Integer>();
				for (int i = 0; i < currentPlayList.getPlaylistSize(); i++) {
					boolean has = false;
					for (int index : indexes) {
						if (i == index) {
							has = true;
						}
					}
					if (has == false) {
						list.add(i);
					}
				}
				int[] selects = new int[list.size()];
				for (int i = 0; i < list.size(); i++) {
					selects[i] = list.get(i);
				}
				rightList.setSelectedIndices(selects);
			}
		});
		return menu;
	}

	private JMenu createModeMenu() {
		JMenu menu = new JMenu(Config.getResource("playlist.mode"));
		ButtonGroup bg1 = new ButtonGroup();
		ButtonGroup bg2 = new ButtonGroup();
		// 不循环
		JRadioButtonMenuItem noCircle = new JRadioButtonMenuItem(Config.getResource("playlist.mode.noCircle"));
		noCircle.setSelected(!config.isRepeatEnabled());
		menu.add(noCircle).addActionListener(new

		ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				config.setRepeatEnabled(false);
			}
		});
		// 单曲循环
		JRadioButtonMenuItem singleCircle = new JRadioButtonMenuItem(Config.getResource("playlist.mode.singleCircle"));
		singleCircle.setSelected(config.isRepeatEnabled() && config.getRepeatStrategy() == Config.REPEAT_ONE);
		menu.add(singleCircle).addActionListener(new

		ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				config.setRepeatEnabled(true);
				config.setRepeatStrategy(Config.REPEAT_ONE);
			}
		});
		// 整体循环
		JRadioButtonMenuItem allCircle = new JRadioButtonMenuItem(Config.getResource("playlist.mode.allCircle"));
		allCircle.setSelected(config.isRepeatEnabled() && config.getRepeatStrategy() == Config.REPEAT_ALL);
		menu.add(allCircle).addActionListener(new

		ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				config.setRepeatEnabled(true);
				config.setRepeatStrategy(Config.REPEAT_ALL);
			}
		});
		menu.addSeparator();
		// 顺序播放
		JRadioButtonMenuItem orderPlay = new JRadioButtonMenuItem(Config.getResource("playlist.mode.orderPlay"));
		orderPlay.setSelected(config.getPlayStrategy() == Config.ORDER_PLAY);
		menu.add(orderPlay).addActionListener(new

		ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				config.setPlayStrategy(Config.ORDER_PLAY);
			}
		});
		// 随机播放
		JRadioButtonMenuItem randomPlay = new JRadioButtonMenuItem(Config.getResource("playlist.mode.randomPlay"));
		randomPlay.setSelected(config.getPlayStrategy() == Config.RANDOM_PLAY);
		menu.add(randomPlay).addActionListener(new

		ActionListener() {

			public void actionPerformed(ActionEvent ae) {

				config

						.setPlayStrategy

				(

						Config

				.RANDOM_PLAY);
			}
		});
		bg1.add(noCircle);
		bg1.add(singleCircle);
		bg1.add(allCircle);
		bg2.add(orderPlay);
		bg2.add(randomPlay);
		return menu;
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	/**
	 * 用于应用程序之间传递数据时用到的对象
	 * 
	 * @param T
	 */
	private class MyData<T> {

		private int oldIndex;
		private T t;

		public MyData(int oldIndex, T t) {
			this.oldIndex = oldIndex;
			this.t = t;
		}

		public int getOldIndex() {
			return oldIndex;
		}

		public T getData() {
			return t;
		}
	}

	/**
	 * 左边列表的渲染器
	 */
	private class LeftListCellRenderer extends JLabel implements ListCellRenderer<Object> {

		private static final long serialVersionUID = 20071214L;
		private volatile boolean hasFocus;

		public LeftListCellRenderer() {
			this.setOpaque(true);
			this.setHorizontalAlignment(JLabel.CENTER);
		}

		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			this.setText(value.toString());
			hasFocus = cellHasFocus;
			if (value.equals(currentPlayList)) {
				isSelected = true;
			} else {
				isSelected = false;
			}
			if (isSelected) {
				setBackground(BG);
				setForeground(HILIGHT);
			} else {
				setBackground(BG);
				setForeground(FORE);
			}
			if (cellHasFocus) {
				setForeground(BG);
				setBackground(FORE);
			}
			return this;
		}

		public void paint(Graphics g) {
			super.paint(g);
			if (hasFocus) {
				g.setColor(HILIGHT);
				g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
			}
		}

		public void repaint() {
		}

		public void repaint(Rectangle rec) {
		}

		public void repaint(long l, int x, int y, int width, int height) {
		}

		public void validate() {
		}

		public void invalidate() {
		}

		public void revalidate() {
		}

		public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
		}
	}

	/**
	 * 右边的列表的渲染器
	 */
	private class RightListCellRenderer extends MOMOLabel implements ListCellRenderer<Object> {

		private static final long serialVersionUID = 20071214L;

		public RightListCellRenderer() {
			this.setOpaque(true);
			this.setBorder(new EmptyBorder(0, 0, 0, 0));
		}

		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			this.setText("judy");
			PlayListItem item = (PlayListItem) value;
			if (item == player.getCurrentItem()) {
				item.setSelected(true);
			} else {
				item.setSelected(false);
			}
			this.setFont(config.getPlaylistFont());
			list.setFont(config.getPlaylistFont());
			this.setPlayListItem(item);
			this.setIsSelected(isSelected && rightHasFocus);
			this.setIndex(index);
			this.setItemCount(currentPlayList.getPlaylistSize());
			// this.setHasFocus(rightHasFocus && (cellHasFocus || (rightIndex ==
			// index)));
			this.setHasFocus(rightHasFocus && (cellHasFocus));
			// if (cellHasFocus || isSelected) {
			//// setBackground(Color.WHITE);
			//// setForeground(Color.BLACK);
			// } else {
			// setForeground(FORE);
			// setBackground(BG);
			// }
			if (index % 2 == 0) {
				setBackground(config.getPlaylistBackground1());
			} else {
				setBackground(config.getPlaylistBackground2());
			}
			return this;
		}

		public void repaint() {
		}

		public void repaint(Rectangle rec) {
		}

		public void repaint(long l, int x, int y, int width, int height) {
		}

		public void validate() {
		}

		public void invalidate() {
		}

		public void revalidate() {
		}

		public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
		}
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
		int index = rightList.locationToIndex(e.getPoint());
		if (index != onIndex) {
			onIndex = index;
			showInfo();
		}
	}

	/**
	 * 显示ToolTip的提示
	 */
	private void showInfo() {
		if (onIndex == -1 || !config.isShowTooltipOnPlayList()) {
			rightList.setToolTipText(null);
			return;
		}
		PlayListItem item = currentPlayList.getItemAt(onIndex);
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append(Config.getResource("songinfo.title")).append(" ").append(item.getTitle()).append("<br>");
		sb.append(Config.getResource("songinfo.artist")).append(" ").append(item.getArtist()).append("<br>");
		sb.append(Config.getResource("songinfo.album")).append(" ").append(item.getAlbum()).append("<br>");
		sb.append(Config.getResource("songinfo.format")).append(" ").append(item.getFormat()).append("<br>");
		sb.append(Config.getResource("songinfo.length")).append(" ").append(item.getFormattedLength()).append("<br>");
		sb.append(Config.getResource("songinfo.location")).append(" ").append(item.getLocation()).append("<br>&nbsp;<p>");
		sb.append("</html>");
		rightList.setToolTipText(sb.toString());
	}
}
