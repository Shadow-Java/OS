import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Queue;
import java.util.Stack;
import java.util.LinkedList;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.util.Vector;

public class Os extends JFrame {
    private JPanel mode_panel=new JPanel(new BorderLayout());//总的控制界面，控制整个程序的执行
    private JPanel mode_panel_1=new JPanel(new GridLayout(1,3));//总控制界面的两个小面板
    private JPanel mode_panel_2=new JPanel(new FlowLayout());
    private JPanel panel1=new JPanel(new BorderLayout());//图形界面一,使用边界布局
    private JPanel time_Panel=new JPanel(new GridLayout(1,2));
    private JPanel panel2=new JPanel(new BorderLayout());//图形界面二
    private JPanel panel3=new JPanel(new BorderLayout());//图形界面三
    private JPanel panel4=new JPanel(new BorderLayout());//图形界面四
    private JPanel panel5=new JPanel(new BorderLayout());//图形界面五

    private JPanel panel_2=new JPanel(new FlowLayout());//展示不同硬件的详情new GridLayout(1,4)
    private JPanel panel_21=new JPanel(new GridLayout(2,1));//图形界面二的两个详情界面
    private JPanel panel_211=new JPanel(new BorderLayout());
    private JPanel panel_2111=new JPanel(new GridLayout(1,2));
    private JPanel panel_2112=new JPanel(new GridLayout(1,2));
    private JPanel panel_233=new JPanel(new GridLayout(1,2));
    private JPanel panel_244=new JPanel(new GridLayout(1,2));
    private JPanel panel_212=new JPanel(new BorderLayout());
    private JPanel panel_32=new JPanel(new FlowLayout());

    private JTextArea txt=new JTextArea(80000,700);//进程详细文本域
    private JLabel cpu_bel=new JLabel("CPU: ");//CPU占用情况
    private JTextField cpu_field=new JTextField(10);
    private JLabel instr_bel=new JLabel("指令:");//指令执行情况
    private JTextField instr_field=new JTextField(25);

    private JLabel time_bel2=new JLabel("时间计时：",JLabel.RIGHT);//JLable相对于输入框的位置

    private JTextField time_field2=new JTextField(10);
    private JLabel resource_bel=new JLabel("资源数量:");
    private JTextField resource_field=new JTextField(25);
    private JLabel memory_bel=new JLabel("内存：",JLabel.LEFT);
    private JTextField memory_field=new JTextField(18);
    private JLabel disk_bel=new JLabel("磁盘：",JLabel.RIGHT);
    private JTextField disk_field=new JTextField(5);
    private JLabel re_man_bel=new JLabel("资源管理:");
    private JLabel deadLock_bel=new JLabel("死锁检测:");
    private JTextArea resource_area =new JTextArea(1000,500);//资源管理详情
    private JTextArea deadLoc_area=new JTextArea(1000,500);//死锁详情
    private JLabel synLbel=new JLabel("进程同步:");
    private JLabel mutexbel=new JLabel("进程互斥:");
    private JTextArea mutex_area =new JTextArea(1000,500);
    private  JTextArea syn_area=new JTextArea(1000,500);//进程互斥

    private JLabel mode_bel=new JLabel("请选择JCB和PCB生成模式：",JLabel.RIGHT);
    public JRadioButton random_buton=new JRadioButton("随机生成",true);
    public JRadioButton read_buton=new JRadioButton("读写文件");
    public JButton run_buton=new JButton("开机");
    private JButton exit_buton=new JButton("退出");
    private ButtonGroup buttonGroup=new ButtonGroup();//设置为单选

    private JScrollPane jScrollPane;//面板一滚动条
    private JScrollPane scrollPane_21;//面板二的两个滚动条
    private JScrollPane scrollPane_22;
    private JScrollPane scrollPane_23;
    private JScrollPane scrollPane_24;
    private JScrollPane scrollPane;//面板三滚动条
    private JScrollPane scrollPane_4;//面板四滚动条
    private JTabbedPane tp = new JTabbedPane();//任务栏
   // private JComboBox dropBox;//定义下拉框
    private JTable table;
    private JTable memoryTabel;

    private JLabel page_short_label =new JLabel("缺页中断",JLabel.CENTER);
    private JTextArea page_short_txt=new JTextArea(80000,500);//缺页中断
    private JScrollPane page_short_scroll;

    private Computer computer;//操作系统管理的裸机

    public boolean memory_table[]=new boolean[64];
    //内存页框表，下标为页框号，true表示该页框被占用，false表示该页框空闲
    public byte page_table[][]=new byte[128][2];//页表,下标为页面号，每个byte有8位

    //第一列：
    // 第0位为分配标志位，0表示该页面空闲，1表示该页面已分配给进程
    // 第1位为驻留标志位，0表示该页面不在内存，1表示该页面已经装入内存
    // 第2-7位表示该页面装入的内存块号
    //第二列：
    // 第0位为保护标志，0表示不受保护，1表示受保护
    // 第1-7位作为LRU-老化算法的页面引用计数器



    public boolean inter_flag;//中断标志位，true为中断，false不能中断
    public Job jobs[];//随机生成的作业序列
    public int job_num;
    public boolean block_flag;//当前是否有进程占用临界资源，false为否，true为是
    public boolean end_flag;//是否执行完所有作业，false为否，true为是
    //private int memory_show;
    private int instr_flag;//表示当前CPU正在执行的指令类型，0表示系统调用，1表示用户态计算操作，2表示PV操作
    private int pv_flag;//表示是否已有进程进行PV操作且未结束，-1表示无，非负表示占用临界资源的进程ID
    private int syn_flag;//同步标志,-1表示不需要同步，非负表示对应序号的进程不需要同步，和对应序号进程同步的进程需要同步
    private PrintStream log;//写系统日志
    private PrintStream memory_log;//写内存占用情况日志
    private int resources[][]=new int[5][5];//共有5种资源，每种资源数量为5，-1表示空闲，非负表示被对应序号进程占有
    private int resource_num[]=new int[5];//共有5种资源，数组存放每种资源的数量

    private Queue<Process>q1=new LinkedList<Process>();//运行队列（数量只能为1或0）
    private Queue<Process>q2=new LinkedList<Process>();//就绪队列
    private Queue<Process>q3=new LinkedList<Process>();//阻塞队列
    private  Queue<Process>q4=new LinkedList<Process>();//已完成的进程
    private Queue<Process>q5=new LinkedList<Process>();//没有空闲页面导致尚未分配完页面需等待的进程
    private Queue<Process>q6=new LinkedList<Process>();//因造成死锁而被撤销的进程
    private Queue<Process>q_re=new LinkedList<Process>();//因为需要资源不足而阻塞的队列 等待分配资源的队列
    private Queue<Integer>num_temp=new LinkedList<Integer>();//储存从PCB文件中读入的数字


    Os(Computer computer){//构造函数
        super("操作系统");

        this.computer=computer;
        inter_flag=false;
        for(int i=0;i<64;i++)
            memory_table[i]=false;
        for(int i=0;i<128;i++) {
            page_table[i][0] = 0;
            page_table[i][1] = 0;
        }

        block_flag=false;
        end_flag=false;
        pv_flag=-1;
        syn_flag=-1;

        //初始化资源数量和占有情况
        for(int i=0;i<5;i++) {
            for (int j = 0; j < 5; j++)
                resources[i][j] = -1;
            resource_num[i]=5;
        }

        //初始化日志输出流
        File directory=new File(".");
        String path=null;
        String memory_path=null;
        try{
            path=directory.getCanonicalPath();
        }catch(IOException e){
            e.printStackTrace();
        }

        memory_path=path;
        path+="\\run_log.txt";
        memory_path+="\\memory_log.txt";
        File logfile=new File(path);
        File memory_logfile=new File(memory_path);
        try {
            if (!logfile.exists())
                logfile.createNewFile();
            if(!memory_logfile.exists())
                memory_logfile.createNewFile();
            FileOutputStream out=new FileOutputStream(logfile);
            log=new PrintStream(out);
            FileOutputStream memory_out=new FileOutputStream(memory_logfile);
            memory_log=new PrintStream(memory_out);
        }catch(IOException e){
            e.printStackTrace();
        }

       setUI();
    }

    public void mode_selectUI(){//界面模式选择，分为两种模式：1.随机选择  2.文本输入
        panel3.add(mode_panel,BorderLayout.SOUTH);
        mode_panel.add(mode_panel_1,BorderLayout.NORTH);
        mode_panel.add(mode_panel_2,BorderLayout.CENTER);
        mode_panel_1.add(mode_bel);
        mode_panel_1.add(random_buton);
        mode_panel_1.add(read_buton);
        mode_panel_2.add(run_buton);
        mode_panel_2.add(exit_buton);
        buttonGroup.add(random_buton);buttonGroup.add(read_buton);
        mode_bel.setFont(new Font("宋体", Font.PLAIN, 20));
        random_buton.setFont(new Font("宋体", Font.PLAIN, 20));
        read_buton.setFont(new Font("宋体", Font.PLAIN, 20));
        exit_buton.setFont(new Font("宋体", Font.PLAIN, 20));
        run_buton.setFont(new Font("宋体", Font.PLAIN, 20));

        run_buton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(random_buton.isSelected()) {
                            mode_run(1);//随机生成作业序列
                            computer.computerstart();
                        }
                        else if(read_buton.isSelected())
                            if(mode_run(2))//从文件读入作业序列
                                computer.computerstart();
                    }
                }
        );

        exit_buton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.exit(0);
                    }
                }
        );


    }



    public void setUI(){
        setSize(1000,1000);//设置界面大小
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        tp.add(panel3);//加入四个面板
        tp.add(panel1);
        tp.add(panel2);
        tp.add(panel4);
        tp.add(panel5);
        tp.setTitleAt(0,"调度过程");//设置宽度
        tp.setTitleAt(1,"进程信息");
        tp.setTitleAt(2,"资源、死锁、同步与互斥");
        tp.setTitleAt(3,"内存");
        tp.setTitleAt(4,"缺页中断");
        tp.setSize(100,200);
        setUI_2();
        setUI_3();
        setUI_4();
        setUI_5();
        setContentPane(tp);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void setUI_1(){//图形界面一
        Object[] columnNames = {"进程名称", "PID", "状态","是否需要同步","是否需要资源","需要的资源","已分配资源","内存","内容描述"};//表头
        int n=0;
        for(int i=0;i<job_num;i++){
            for(int j=0;j<jobs[i].task_num;j++)
                n++;
        }
        Object[][] rowData=new Object[n][9];//表格数据

        int m=0;//PID
        for(int i=0;i<job_num;i++){

            for(int j=0;j<jobs[i].task_num;j++){//每个任务即每个进程
                Task task=jobs[i].task_list[j];//每个任务的进程
                rowData[m][0]="进程";
                rowData[m][1]=m;  //PID
                rowData[m][2]="就绪态";
                rowData[m][3]="是否需要同步";
                rowData[m][4]="是否需要资源";
                rowData[m][5]="需要的资源数量";
                rowData[m][6]="已分配资源数量";
                rowData[m][7]=task.size+"B";
                rowData[m][8]="含有"+task.instrucnum+"条指令";
                m++;
            }
        }

         table=new JTable(new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return rowData.length;
            }

            @Override
            public int getColumnCount() {
                return rowData[0].length;
            }
             @Override
             public String getColumnName(int column) {
                 return columnNames[column].toString();
             }

             @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return rowData[rowIndex][columnIndex];
            }
            @Override
            public void setValueAt(Object newValue, int rowIndex, int columnIndex){
                rowData[rowIndex][columnIndex]=newValue;
                fireTableCellUpdated(rowIndex,columnIndex);//修改单元格
            }
        });



        //JTable table=new JTable(rowData,columnNames);//指定表头和所有行数据
        table.setFont(new Font(null,Font.PLAIN,14));//设置字体样式
        table.setSelectionForeground(Color.DARK_GRAY);//设置选中后字体颜色
        table.setSelectionBackground(Color.LIGHT_GRAY);//设置选中后字体背景
        table.setGridColor(Color.GRAY);//网格颜色
        table.setBackground(Color.WHITE);
        //设置表头
        table.getTableHeader().setFont(new Font(null,Font.BOLD,14));//设置表头字体样式
        table.getTableHeader().setForeground(Color.RED);//设置表头字体颜色
        table.getTableHeader().setResizingAllowed(false);//设置不允许手动改变列宽
        table.getTableHeader().setReorderingAllowed(false);//设置不允许拖动重新排列各序

        table.setRowHeight(40);//设置行高
        table.getColumnModel().getColumn(0).setPreferredWidth(40);//第一列列宽设置为40
        table.setPreferredScrollableViewportSize(new Dimension(400,300));//设置滚动面案视口大小（超过改大小的行数据，需要拖动滚动条才能看到）
        jScrollPane=new JScrollPane(table);//滚动条

        panel1.add(jScrollPane,BorderLayout.CENTER);
        panel1.add(time_Panel,BorderLayout.SOUTH);
        time_Panel.add(time_bel2,0);
        time_Panel.add(time_field2,1);
        time_field2.setFont(new Font(null,Font.BOLD,14));
        time_field2.setBackground(new Color(128,118,105));
        time_field2.setHorizontalAlignment(JTextField.CENTER);
        time_field2.setEnabled(false);
        time_bel2.setFont(new Font(null,Font.BOLD,14));
        time_bel2.setForeground(Color.RED);
        time_bel2.setSize(40,300);

    }


    public void setUI_2(){//展示四种种不同调度情况

        panel2.add(BorderLayout.NORTH,panel_2);//加入组件
        panel2.add(BorderLayout.CENTER,panel_21);

        scrollPane_21=new JScrollPane(resource_area);
        scrollPane_22=new JScrollPane(mutex_area);
        scrollPane_23=new JScrollPane(deadLoc_area);
        scrollPane_24=new JScrollPane(syn_area);
        scrollPane_21.setHorizontalScrollBarPolicy(//设置水平滚动天总是隐藏
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane_22.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane_23.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane_24.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        panel_21.add(panel_211);panel_21.add(panel_212);
        panel_211.add(panel_233,BorderLayout.NORTH);panel_211.add(panel_2111,BorderLayout.CENTER);//位置
        panel_212.add(panel_244,BorderLayout.NORTH);panel_212.add(panel_2112,BorderLayout.CENTER);
        panel_233.add(re_man_bel);panel_233.add(deadLock_bel);
        panel_244.add(synLbel);panel_244.add(mutexbel);
        panel_2111.add(scrollPane_21);panel_2111.add(scrollPane_23);
        panel_2112.add(scrollPane_24);panel_2112.add(scrollPane_22);


        resource_field.setEnabled(false);
        memory_field.setEnabled(false);
        disk_field.setEnabled(false);
        panel_2.add(resource_bel);panel_2.add(resource_field);
        panel_2.add(memory_bel);panel_2.add(memory_field);
        panel_2.add(disk_bel);panel_2.add(disk_field);
        resource_bel.setFont(new Font("宋体", Font.PLAIN, 20));
        resource_bel.setForeground(Color.RED);
        memory_bel.setFont(new Font("宋体", Font.PLAIN, 20));
        memory_bel.setForeground(Color.RED);
        disk_bel.setFont(new Font("宋体", Font.PLAIN, 20));
        disk_bel.setForeground(Color.RED);
        deadLock_bel.setFont(new Font("宋体", Font.PLAIN, 20));
        deadLock_bel.setForeground(Color.BLUE);
        re_man_bel.setFont(new Font("宋体", Font.PLAIN, 20));
        re_man_bel.setForeground(Color.BLUE);
        synLbel.setFont(new Font("宋体",Font.PLAIN,20));
        synLbel.setForeground(Color.BLUE);
        mutexbel.setFont(new Font("宋体",Font.PLAIN,20));
        mutexbel.setForeground(Color.BLUE);
        memory_field.setHorizontalAlignment(JTextField.CENTER);
        disk_field.setHorizontalAlignment(JTextField.CENTER);
        resource_field.setHorizontalAlignment(JTextField.CENTER);

        memory_field.setText("32KB");
        memory_field.setFont(new Font("隶书",Font.BOLD,15));
        memory_field.setForeground(Color.GRAY);
        disk_field.setText("1MB");
        disk_field.setFont(new Font("隶书",Font.BOLD,20));
        disk_field.setForeground(Color.GRAY);

       // resource_field.setText("A:5 | B:3 | C:3 | D:4 | E:3");
        resource_field.setFont(new Font("隶书",Font.BOLD,15));
        resource_field.setForeground(Color.GRAY);
        deadLoc_area.setFont(new Font("黑体",Font.BOLD,20));//死锁文本域字体设置
        resource_area.setFont(new Font("黑体",Font.BOLD,25));//资源文本域字体设置
        syn_area.setFont(new Font("隶书",Font.BOLD,25));//同步文本域字体设置
        mutex_area.setFont(new Font("隶书",Font.BOLD,16));//互斥文本域字体设置
        deadLoc_area.setEnabled(false);
        resource_area.setEnabled(false);
        syn_area.setEnabled(false);
        mutex_area.setEnabled(false);

        mutex_area.setBackground(new Color(128,118,105));
        deadLoc_area.setBackground(new Color(128,118,105));
        resource_area.setBackground(new Color(128,118,105));
        syn_area.setBackground(new Color(128,118,105));

    }


    public void setUI_3(){
        txt.setLineWrap(true); //设置自动换行
        txt.setBackground(new Color(128,118,105));
        txt.setForeground(Color.WHITE); //背景颜色
        txt.setEditable(false);//不能编辑
        scrollPane=new JScrollPane(txt);
        txt.setPreferredSize(new Dimension(400,300));

        txt.setFont(new Font("隶书",Font.PLAIN,15));

        //分别设置水平和垂直滚动条自动出现
        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        //scrollPane.setBounds(0,187,591,98);
        panel3.add(scrollPane,BorderLayout.CENTER);
        panel3.add(panel_32,BorderLayout.NORTH);
        //panel3.add(mode_panel);
        mode_selectUI();
        cpu_field.setEnabled(false);
        cpu_field.setFont(new Font("黑体",Font.BOLD,25));
        cpu_bel.setFont(new Font("小篆",Font.PLAIN,20));
        cpu_field.setHorizontalAlignment(JTextField.CENTER);
        instr_bel.setFont(new Font("黑体",Font.PLAIN,25));
        instr_field.setEnabled(false);
        instr_field.setFont(new Font("黑体",Font.BOLD,25));
        instr_field.setHorizontalAlignment(JTextField.CENTER);
        panel_32.add(cpu_bel);panel_32.add(cpu_field);panel_32.add(instr_bel);panel_32.add(instr_field);
    }


    public void setUI_4(){//展示内存情况

        Object[] columnNames_2 = {"物理块号", "占用情况", "页面号"};//表头
        Object[][] rowData_2=new Object[64][3];//表格数据
        for(int i=0;i<64;i++){
            rowData_2[i][0]=i;
            rowData_2[i][1]="空闲";
            rowData_2[i][2]="无";
        }

        memoryTabel=new JTable(new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return rowData_2.length;
            }

            @Override
            public int getColumnCount() {
                return rowData_2[0].length;
            }
            @Override
            public String getColumnName(int column) {
                return columnNames_2[column].toString();
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return rowData_2[rowIndex][columnIndex];
            }
            @Override
            public void setValueAt(Object newValue, int rowIndex, int columnIndex){
                rowData_2[rowIndex][columnIndex]=newValue;
                fireTableCellUpdated(rowIndex,columnIndex);//修改单元格
            }
        });
        memoryTabel.setFont(new Font(null,Font.PLAIN,14));//设置字体样式
        memoryTabel.setSelectionForeground(Color.DARK_GRAY);//设置选中后字体颜色
        memoryTabel.setSelectionBackground(Color.LIGHT_GRAY);//设置选中后字体背景
        memoryTabel.setGridColor(Color.GRAY);//网格颜色
        memoryTabel.setBackground(Color.WHITE);
        //设置表头
        memoryTabel.getTableHeader().setFont(new Font(null,Font.BOLD,14));//设置表头字体样式
        memoryTabel.getTableHeader().setForeground(Color.RED);//设置表头字体颜色
        memoryTabel.getTableHeader().setResizingAllowed(false);//设置不允许手动改变列宽
        memoryTabel.getTableHeader().setReorderingAllowed(false);//设置不允许拖动重新排列各序

        memoryTabel.setRowHeight(40);//设置行高
        memoryTabel.getColumnModel().getColumn(0).setPreferredWidth(40);//第一列列宽设置为40
        memoryTabel.setPreferredScrollableViewportSize(new Dimension(400,300));//设置滚动面案视口大小（超过改大小的行数据，需要拖动滚动条才能看到）
        scrollPane_4=new JScrollPane(memoryTabel);
        panel4.add(scrollPane_4,BorderLayout.CENTER);
    }

    void setUI_5(){
        page_short_scroll=new JScrollPane(page_short_txt);
        page_short_scroll.setHorizontalScrollBarPolicy(//设置水平滚动天总是隐藏
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        page_short_txt.setEnabled(false);
        page_short_txt.setFont(new Font("隶书",Font.BOLD,22));
        page_short_label.setFont(new Font("楷书",Font.BOLD,25));
        page_short_label.setForeground(Color.RED);
        panel5.add(page_short_label,BorderLayout.NORTH);
        panel5.add(page_short_scroll,BorderLayout.CENTER);
    }

    public void osstart()throws InterruptedException{//启动操作系统
        for(;;){
            synchronized (computer.clock) {
                if(!inter_flag) {//线程同步，确保每次时钟中断都进行了调度
                    computer.clock.wait();
                }
                cpu_manage();
                gui();
                inter_flag = false;
                computer.clock.notifyAll();
                if (end_flag){
                    computer.disk.write();
                    log.close();
                    break;
                }

            }

        }

    }



    public void memory_manage(Process process,int mode){//内存管理

        //mode=1，创建进程时调用，为进程分配页面，若此时有空余页框，则将页面装入内存
        //mode=2，开始执行进程时调用，每执行一条指令也要调用一次确保将执行到的指令所在页面和进程所需数据所在页面装入内存，
        //        并将逻辑地址转换为物理地址
        //mode=3，撤销进程时调用，回收进程所用内存和页面
        //mode=4，输出进程执行到指令的物理地址和要访问的数据区的物理地址

        switch (mode){
            case 1: {
                boolean flag=true;
                for (int i = 0; i < process.page_num; i++) {
                    if(process.pages[i]==-1) {//尚未分配页面
                        flag = false;

                        for (int j = 0; j < 128; j++) {//寻找尚未分配出去的页面
                            if ((page_table[j][0] & 128) == 0) {//分配标志位为0
                                page_table[j][0] = (byte) (page_table[j][0] | 128);//将分配标志位置1
                                process.pages[i] = j;
                                for(int k=0;k<32;k++){//在硬盘中开辟缓存区存储进程
                                    boolean disk_flag=false;
                                    for(int l=0;l<64;l++){

                                        if(computer.disk.disk[k][l]==-1){

                                            computer.disk.disk[k][l]=process.ProID;

                                            disk_flag=true;

                                            break;

                                        }

                                    }

                                    if(disk_flag)

                                        break;

                                }

                                flag = true;

                                break;

                            }

                        }

                        if (flag == false) {

                            break;

                        }

                    }

                }

                if(flag==false){//没有空闲页面
                    System.out.println("没有空闲页面！需等待其他进程执行完释放页面！");
                    log.println("没有空闲页面！需等待其他进程执行完释放页面！");
                    txt.append("没有空闲页面！需等待其他进程执行完释放页面！\n");
                    process.ProState=4;//进程状态为等待分配页面
                    q5.offer(process);
                }else {
                    for (int i = 0; i < process.page_num; i++) {
                        boolean mem_flag = false;
                        for (int j = 0; j < 64; j++) {//寻找空闲的页框
                            if (!memory_table[j]) {//该页框空闲
                                page_table[process.pages[i]][0] = (byte) (page_table[process.pages[i]][0] | 64);

                                //将页表项的驻留标志位改为1

                                page_table[process.pages[i]][0] = (byte) (page_table[process.pages[i]][0] & 192);

                                page_table[process.pages[i]][0] += (byte) j;

                                //将页面所分得页框的页框号写入该页表项的2-7位

                                memory_table[j] = true;//表示该页框已被占用

                                mem_flag = true;

                                break;

                            }

                        }

                        if (mem_flag == false) {

                            System.out.println("没有空闲页框！");

                            log.println("没有空闲页框！");
                            txt.append("没有空闲页框！\n");

                            break;

                        }

                    }

                    System.out.print(computer.clock.gettime()+"时刻|创建"+process.ProID+"号进程|分配页面号:");

                    log.print(computer.clock.gettime()+"时刻|创建"+process.ProID+"号进程|分配页面号:");
                    txt.append(computer.clock.gettime()+"时刻|创建"+process.ProID+"号进程|分配页面号:");

                    for(int i=0;i<process.page_num;i++) {

                        System.out.print(process.pages[i] + " ");

                        log.print(process.pages[i] + " ");
                        txt.append(process.pages[i] + " ");

                    }

                    System.out.println("");

                    log.println("");

                    txt.append("\n");
                    process.PSW=0;//从进程的第1条指令开始执行

                    process.intime=computer.clock.gettime();



                    if(process.resource_flag){//如果进程需要分配资源

                        if(allocate(process)) {

                            process.ProState = 2;//创建好的进程为就绪态

                            q2.offer(process);

                        } else{

                            process.ProState=3;//资源不够分配，进程为阻塞态

                            q_re.offer(process);//进程被阻塞

                        }

                    }else {//如果进程不需要分配资源

                        process.ProState = 2;//创建好的进程为就绪态

                        q2.offer(process);//创建好的进程进入就绪队列

                    }

                }

                break;

            }

            case 2: {

                int instr_add = process.PSW * 2;//当前执行到指令的逻辑地址

                int instr_page_num = instr_add / 512;//当前执行到指令的逻辑地址所在该进程分得的第几个页面

                int instr_page = process.pages[instr_page_num];//当前执行到指令的逻辑地址所在页面号

                page_table[instr_page][1] = (byte) (page_table[instr_page][1] | 128);//将该页面保护位置1



                if (process.instruc_list[process.PSW].data_flag == 1) {//若当前执行到的指令要访问数据区

                    int data_add_s = process.instrucnum * 2;//进程的数据区的起始逻辑地址

                    int data_add_e = process.data_size + data_add_s-1;//进程的数据区的结束逻辑地址

                    int data_page_s_num = data_add_s / 512;//进程的数据区起始所在该进程分得的第几个页面

                    int data_page_e_num = data_add_e / 512;//进程的数据区结束所在该进程分得的第几个页面

                    for (int i = data_page_s_num; i <= data_page_e_num; i++)//将数据区分得的几个页面保护位置1

                        page_table[process.pages[i]][1] = (byte) (page_table[process.pages[i]][1] | 128);



                    if ((page_table[instr_page][0] & 64) == 0) {//如果指令所在页面没有装入内存

                        page_in(instr_page);//将指令所在页面装入内存

                        page_table[instr_page][1] = (byte) (page_table[instr_page][1] | 64);

                        page_table[instr_page][1] = (byte) (page_table[instr_page][1] & 192);

                        //引用该页面，计数器最左位置1，计数器其他位置0

                    }

                    else//如果指令所在页面已装入内存

                        page_table[instr_page][1] = (byte) (page_table[instr_page][1] | 64);

                    //引用该页面，计数器最左位置1



                    for (int i = data_page_s_num; i <= data_page_e_num; i++) {//检查数据区分得的几个页面
                        if ((page_table[process.pages[i]][0] & 64) == 0) {//如果该页面没有装入内存
                            page_in(process.pages[i]);//将该页面装入内存
                            page_table[process.pages[i]][1] = (byte) (page_table[process.pages[i]][1] | 64);
                            page_table[process.pages[i]][1] = (byte) (page_table[process.pages[i]][1] & 192);
                            //引用该页面，计数器最左位置1，计数器其他位置0

                        }
                        else//如果指该页面已装入内存
                            page_table[process.pages[i]][1] = (byte) (page_table[process.pages[i]][1] | 64);
                        //引用该页面，计数器最左位置1
                    }



                    //确保所有页面都装入内存后解除保护，将指令所在页面和数据区所在页面保护位均置为0
                    page_table[instr_page][1] = (byte) (page_table[instr_page][1] & 127);
                    for (int i = data_page_s_num; i <= data_page_e_num; i++)
                        page_table[process.pages[i]][1] = (byte) (page_table[process.pages[i]][1] & 127);
                }

                else{//若当前执行到的指令无需访问数据区

                    if ((page_table[instr_page][0] & 64) == 0) {//如果指令所在页面没有装入内存
                        page_in(instr_page);//将指令所在页面装入内存
                        page_table[instr_page][1] = (byte) (page_table[instr_page][1] | 64);

                        page_table[instr_page][1] = (byte) (page_table[instr_page][1] & 192);

                        //引用该页面，计数器最左位置1，计数器其他位置0

                    }

                    else//如果指令所在页面已装入内存
                        page_table[instr_page][1] = (byte) (page_table[instr_page][1] | 64);
                    //引用该页面，计数器最左位置1

                    //确保所有页面都装入内存后解除保护，将指令所在页面保护位置为0
                    page_table[instr_page][1] = (byte) (page_table[instr_page][1] & 127);
                }
                break;
            }

            case 3:{
                for(int i=0;i<process.page_num;i++){
                    if((page_table[process.pages[i]][0]&64)==64){//若该页面已分配页框
                        int j=page_table[process.pages[i]][0]&63;//求出该页面分配的页框号
                        memory_table[j]=false;//释放占用页框
                        page_table[process.pages[i]][0]=(byte)(page_table[process.pages[i]][0]&191);
                        //将该的页面的驻留标志位改为0

                    }
                    page_table[process.pages[i]][0]=(byte)(page_table[process.pages[i]][0]&127);
                    //将该的页面的分配标志位改为0
                }
                break;
            }

            case 4: {
                int instr_add = process.PSW * 2;//当前执行到指令的逻辑地址
                int instr_page_num = instr_add / 512;//当前执行到指令的逻辑地址所在该进程分得的第几个页面
                int page_shift = instr_add % 512;//当前执行到指令的地址页内偏移
                int instr_page = process.pages[instr_page_num];//当前执行到指令的逻辑地址所在页面号
                int memory_num = (page_table[instr_page][0] & 63);//求出当前执行到指令的逻辑地址所在页面装入的页框号
                int phy_add = memory_num * 512 + page_shift;//求出当前执行到指令的物理地址
                System.out.print("(物理地址" + phy_add + ")");
                log.print("(物理地址" + phy_add + ")");
                txt.append("(物理地址" + phy_add + ")");
                instr_field.setText(instr_field.getText()+"(物理地址" + phy_add + ")");
                if (process.instruc_list[process.PSW].data_flag == 1) {//若当前执行到的指令要访问数据区
                    int data_add_s = process.instrucnum * 2;//进程的数据区的起始逻辑地址
                    int data_add_e = process.data_size + data_add_s - 1;//进程的数据区的结束逻辑地址
                    int data_page_s = data_add_s / 512;//进程的数据区起始所在该进程分得的第几个页面
                    int data_page_e = data_add_e / 512;//进程的数据区结束所在该进程分得的第几个页面
                    System.out.print("(需要访问数据区:");
                    log.print("(需要访问数据区:");
                    txt.append("(需要访问数据区:");
                    for(int i=data_page_s;i<=data_page_e;i++){//遍历进程数据区分得的所有页面
                        if(i==data_page_s){
                            int shift=data_add_s%512;
                            int memory_data_num=(page_table[process.pages[i]][0]&63);//该页面装入的页框号
                            int phy_data_add_s=memory_data_num*512+shift;//求出起始物理地址
                            int phy_data_add_e=memory_data_num*512+511;//求出结束物理地址
                            System.out.print(phy_data_add_s+"-"+phy_data_add_e+" ");
                            log.print(phy_data_add_s+"-"+phy_data_add_e+" ");
                            txt.append(phy_data_add_s+"-"+phy_data_add_e+" ");
                        }else if(i==data_page_e){
                            int shift=data_add_e%512;
                            int memory_data_num=(page_table[process.pages[i]][0]&63);//该页面装入的页框号
                            int phy_data_add_s=memory_data_num*512;//求出起始物理地址
                            int phy_data_add_e=memory_data_num*512+shift;//求出结束物理地址
                            System.out.print(phy_data_add_s+"-"+phy_data_add_e+" ");
                            log.print(phy_data_add_s+"-"+phy_data_add_e+" ");
                            txt.append(phy_data_add_s+"-"+phy_data_add_e+" ");
                        }else{
                            int memory_data_num=(page_table[process.pages[i]][0]&63);//该页面装入的页框号
                            int phy_data_add_s=memory_data_num*512;//求出起始物理地址
                            int phy_data_add_e=memory_data_num*512+511;//求出结束物理地址
                            System.out.print(phy_data_add_s+"-"+phy_data_add_e+" ");
                            log.print(phy_data_add_s+"-"+phy_data_add_e+" ");
                            txt.append(phy_data_add_s+"-"+phy_data_add_e+" ");
                        }
                    }
                    System.out.print(")");
                    log.print(")");
                    txt.append(")");
                }
            }
        }

    }


    public void cpu_manage(){//处理器管理，时间片为500ms
        int nowtime=computer.clock.gettime();
        if(nowtime%1000==0)page_use_move();//每过1000ms将每个在内存中页面的引用计数器右移1位
        for(int i=0;i<job_num;i++){//为进入系统的作业创建进程
            if(jobs[i].intime==nowtime){
                for(int j=0;j<jobs[i].task_num;j++)
                    create(jobs[i].task_list[j]);
                break;
            }

        }



        //进程调度
        if(q1.size()==0&&q2.size()==0&&q3.size()==0&&nowtime>jobs[job_num-1].intime) {//当前没有进程在运行且没有进程就绪且没有进程阻塞且不会再有进程创建
            end_flag=true;//说明执行完所有作业

        }else{

            if(q1.size()!=0){//有进程正在运行

                if(q1.peek().resource_flag==false||computer.cpu.PC!=q1.peek().instrucnum/2||allocate(q1.peek())) {
                    //若进程不需要分配资源或分配资源成功



                    if (computer.cpu.PC == q1.peek().instrucnum) {//进程已执行完所有指令

                        destroy();//撤销进程

                    } else {
                        if (q1.peek().syn_flag == -1) {//若进程不需要同步
                            memory_manage(q1.peek(), 2);
                            instr_flag = q1.peek().instruc_list[computer.cpu.PC].Instruc_State;
                            System.out.print(computer.clock.gettime() + "时刻|执行" + q1.peek().ProID +
                                    "号进程" + computer.cpu.PC + "号指令");
                            log.print(computer.clock.gettime() + "时刻|执行" + q1.peek().ProID +
                                    "号进程" + computer.cpu.PC + "号指令");
                            txt.append(computer.clock.gettime() + "时刻|执行" + q1.peek().ProID +
                                    "号进程" + computer.cpu.PC + "号指令");
                            instr_field.setText(computer.cpu.PC + "号指令");
                            memory_manage(q1.peek(),4);
                            System.out.print("——");
                            log.print("——");
                            txt.append("——");

                            if (instr_flag == 0) {//若执行的是系统调用指令

                                System.out.println("系统调用指令，系统处于内核态");

                                log.println("系统调用指令，系统处于内核态");
                                txt.append("系统调用指令，系统处于内核态\n");

                                q1.peek().instruc_list[computer.cpu.PC].needtime -= 10;
                                if (q1.peek().instruc_list[computer.cpu.PC].needtime <= 0) {
                                    computer.cpu.PC++;
                                    q1.peek().PSW++;
                                    run_ready();
                                }

                            } else if (instr_flag == 1) {//若执行的是用户态计算指令

                                System.out.println("用户态计算指令，系统处于用户态");

                                log.println("用户态计算指令，系统处于用户态");
                                txt.append("用户态计算指令，系统处于用户态\n");

                                q1.peek().instruc_list[computer.cpu.PC].needtime -= 10;

                                if (q1.peek().instruc_list[computer.cpu.PC].needtime <= 0) {

                                    computer.cpu.PC++;

                                    q1.peek().PSW++;

                                }

                                int runtime = nowtime - q1.peek().starttime;

                                if (runtime >= 500) {//用完时间片

                                    System.out.println(computer.clock.gettime() + "时刻|" + q1.peek().ProID + "号进程用完时间片");

                                    log.println(computer.clock.gettime() + "时刻|" + q1.peek().ProID + "号进程用完时间片");
                                    txt.append(computer.clock.gettime() + "时刻|" + q1.peek().ProID + "号进程用完时间片\n");
                                    run_ready();
                                }

                            } else if (instr_flag == 2) {//若执行的是PV操作指令

                                if (pv_flag == -1 || pv_flag == q1.peek().ProID) {//临界资源空闲

                                    System.out.println("PV操作指令，将临界资源分配给该进程");
                                    log.println("PV操作指令，将临界资源分配给该进程");
                                    txt.append("PV操作指令，将临界资源分配给该进程\n");

                                    if (pv_flag == -1) {
                                        pv_flag = q1.peek().ProID;//将临界资源分配给该进程
                                        mutex_area.append(computer.clock.gettime() + "时刻|" + q1.peek().ProID +
                                                "号进程请求互斥信号量,该进程获得互斥信号量\n");
                                    }

                                    q1.peek().instruc_list[computer.cpu.PC].needtime -= 10;

                                    if (q1.peek().instruc_list[computer.cpu.PC].needtime <= 0) {

                                        pv_flag = -1;
                                        mutex_area.append(computer.clock.gettime() + "时刻|" + q1.peek().ProID +
                                                "号进程释放互斥信号量\n");

                                        if (q3.size() != 0) wake();

                                        computer.cpu.PC++;

                                        q1.peek().PSW++;

                                    }

                                    int runtime = nowtime - q1.peek().starttime;

                                    if (runtime >= 500) {

                                        System.out.println(computer.clock.gettime() + "时刻|" + q1.peek().ProID + "号进程用完时间片");

                                        log.println(computer.clock.gettime() + "时刻|" + q1.peek().ProID + "号进程用完时间片");
                                        txt.append(computer.clock.gettime() + "时刻|" + q1.peek().ProID + "号进程用完时间片\n");

                                        run_ready();

                                    }

                                } else {//临界资源被占用

                                    System.out.println("PV操作指令，临界资源被其他进程占用，该进程进入阻塞队列");

                                    log.println("PV操作指令，临界资源被其他进程占用，该进程进入阻塞队列");
                                    txt.append("PV操作指令，临界资源被其他进程占用，该进程进入阻塞队列\n");
                                    mutex_area.append(computer.clock.gettime() + "时刻|" + q1.peek().ProID +
                                            "号进程请求互斥信号量,互斥信号量被"+pv_flag+"号进程占用,该进程进入阻塞队列\n");
                                    run_wait();

                                }

                            }

                        } else if (syn_flag == q1.peek().ProID) {//若进程需要同步但暂时不用阻塞

                            memory_manage(q1.peek(), 2);

                            instr_flag = q1.peek().instruc_list[computer.cpu.PC].Instruc_State;

                            System.out.print(computer.clock.gettime() + "时刻|执行" + q1.peek().ProID +
                                    "号进程" + computer.cpu.PC + "号指令");
                            log.print(computer.clock.gettime() + "时刻|执行" + q1.peek().ProID +
                                    "号进程" + computer.cpu.PC + "号指令");
                            txt.append(computer.clock.gettime() + "时刻|执行" + q1.peek().ProID +
                                    "号进程" + computer.cpu.PC + "号指令");
                            instr_field.setText(computer.cpu.PC + "号指令");
                            memory_manage(q1.peek(),4);
                            System.out.print("——");
                            log.print("——");
                            txt.append("——");

                            if (instr_flag == 0) {//若执行的是系统调用指令

                                System.out.println("系统调用指令，系统处于内核态");

                                log.println("系统调用指令，系统处于内核态");
                                txt.append("系统调用指令，系统处于内核态\n");

                                q1.peek().instruc_list[computer.cpu.PC].needtime -= 10;

                                if (q1.peek().instruc_list[computer.cpu.PC].needtime <= 0) {

                                    if (computer.cpu.PC == q1.peek().instrucnum / 2 || computer.cpu.PC == q1.peek().instrucnum - 1)

                                        syn_flag = q1.peek().syn_flag;

                                    computer.cpu.PC++;

                                    q1.peek().PSW++;

                                    run_ready();

                                }

                            } else if (instr_flag == 1) {//若执行的是用户态计算指令

                                System.out.println("用户态计算指令，系统处于用户态");

                                log.println("用户态计算指令，系统处于用户态");
                                txt.append("用户态计算指令，系统处于用户态\n");

                                q1.peek().instruc_list[computer.cpu.PC].needtime -= 10;

                                if (q1.peek().instruc_list[computer.cpu.PC].needtime <= 0) {

                                    if (computer.cpu.PC == q1.peek().instrucnum / 2 || computer.cpu.PC == q1.peek().instrucnum - 1)

                                        syn_flag = q1.peek().syn_flag;

                                    computer.cpu.PC++;

                                    q1.peek().PSW++;

                                }

                                int runtime = nowtime - q1.peek().starttime;

                                if (runtime >= 500) {//用完时间片

                                    System.out.println(computer.clock.gettime() + "时刻|" + q1.peek().ProID + "号进程用完时间片");

                                    log.println(computer.clock.gettime() + "时刻|" + q1.peek().ProID + "号进程用完时间片");
                                    txt.append(computer.clock.gettime() + "时刻|" + q1.peek().ProID + "号进程用完时间片\n");

                                    run_ready();

                                }

                            } else if (instr_flag == 2) {//若执行的是PV操作指令

                                if (pv_flag == -1 || pv_flag == q1.peek().ProID) {//临界资源空闲

                                    System.out.println("PV操作指令，将临界资源分配给该进程");

                                    log.println("PV操作指令，将临界资源分配给该进程");
                                    txt.append("PV操作指令，将临界资源分配给该进程\n");

                                    if (pv_flag == -1) {
                                        pv_flag = q1.peek().ProID;//将临界资源分配给该进程
                                        mutex_area.append(computer.clock.gettime() + "时刻|" + q1.peek().ProID +
                                                "号进程请求互斥信号量,该进程获得互斥信号量\n");
                                    }

                                    q1.peek().instruc_list[computer.cpu.PC].needtime -= 10;

                                    if (q1.peek().instruc_list[computer.cpu.PC].needtime <= 0) {

                                        if (computer.cpu.PC == q1.peek().instrucnum / 2 || computer.cpu.PC == q1.peek().instrucnum - 1)

                                            syn_flag = q1.peek().syn_flag;

                                        pv_flag = -1;
                                        mutex_area.append(computer.clock.gettime() + "时刻|" + q1.peek().ProID +
                                                "号进程释放互斥信号量\n");

                                        if (q3.size() != 0) wake();

                                        computer.cpu.PC++;

                                        q1.peek().PSW++;

                                    }

                                    int runtime = nowtime - q1.peek().starttime;

                                    if (runtime >= 500) {

                                        System.out.println(computer.clock.gettime() + "时刻|" + q1.peek().ProID + "号进程用完时间片");

                                        log.println(computer.clock.gettime() + "时刻|" + q1.peek().ProID + "号进程用完时间片");
                                        txt.append(computer.clock.gettime() + "时刻|" + q1.peek().ProID + "号进程用完时间片\n");

                                        run_ready();

                                    }

                                } else {//临界资源被占用

                                    System.out.println("PV操作指令，临界资源被其他进程占用，该进程进入阻塞队列");

                                    log.println("PV操作指令，临界资源被其他进程占用，该进程进入阻塞队列");
                                    txt.append("PV操作指令，临界资源被其他进程占用，该进程进入阻塞队列\n");
                                    mutex_area.append(computer.clock.gettime() + "时刻|" + q1.peek().ProID +
                                            "号进程请求互斥信号量,互斥信号量被"+pv_flag+"号进程占用,该进程进入阻塞队列\n");
                                    run_wait();

                                }

                            }

                        } else {

                            System.out.println(computer.clock.gettime() + "时刻|" + q1.peek().ProID + "号进程需要和" +

                                    q1.peek().syn_flag + "号进程同步，暂停运行");

                            log.println(computer.clock.gettime() + "时刻|" + q1.peek().ProID + "号进程需要和" +

                                    q1.peek().syn_flag + "号进程同步，暂停运行");
                            syn_area.append(computer.clock.gettime() + "时刻|" + q1.peek().ProID + "号进程需要和" +

                                    q1.peek().syn_flag + "号进程同步，暂停运行\n");
                            run_ready();

                        }

                    }

                }else{//进程需要分配资源且分配资源失败
                    q1.peek().ProState=3;//进程为阻塞态
                    q_re.offer(q1.poll());//进程被阻塞
                    if(q2.size()!=0){
                        q1.offer(q2.poll());
                        q1.peek().starttime = computer.clock.gettime();
                        computer.cpu.PC=q1.peek().PSW;
                        q1.peek().ProState=1;//进程为运行态
                    }
                }

            }else{//当前CPU空闲

                if(q2.size()!=0){
                    q1.offer(q2.poll());
                    q1.peek().starttime = computer.clock.gettime();
                    computer.cpu.PC=q1.peek().PSW;
                    q1.peek().ProState=1;//进程为运行态
                }
            }
            /*if(q1.size()!=0&&q1.peek().ProID==1)
                deadLoc_area.append(String.valueOf(pv_flag)+"   "+String.valueOf(q1.peek().PSW)+
                        "  "+String.valueOf(computer.cpu.PC)+"\n");
            if(pv_flag==-1&&q3.size()!=0)//如果互斥信号量没有被占用且有进程在阻塞
                wake();*/
            deadlock();//死锁检测与撤销

        }

    }


    private void run_ready(){

        if (q2.size() != 0) {
            q1.peek().PSW = computer.cpu.PC;
            q1.peek().ProState=2;//进程变为就绪态
            q2.offer(q1.poll());
            q1.offer(q2.poll());
            q1.peek().starttime = computer.clock.gettime();
            computer.cpu.PC = q1.peek().PSW;
            if(computer.cpu.PC==q1.peek().instrucnum){//进程已执行完所有指令
                destroy();//撤销进程
            }else {
                memory_manage(q1.peek(), 2);
            }

        } else {
            q1.peek().starttime = computer.clock.gettime();
        }
    }


    private void run_wait(){

        q1.peek().PSW=computer.cpu.PC;

        q1.peek().ProState=3;//进程变为阻塞态

        q3.offer(q1.poll());

        if(q2.size()!=0){
            q1.offer(q2.poll());
            q1.peek().starttime = computer.clock.gettime();
            q1.peek().ProState=1;//进程变为运行态
            computer.cpu.PC=q1.peek().PSW;
            if(computer.cpu.PC==q1.peek().instrucnum){//进程已执行完所有指令

                destroy();//撤销进程

            }else {

                memory_manage(q1.peek(), 2);

            }

        }

    }


    public void create(Task task){//进程创建原语

        Process process=new Process();
        process.ProID=Process.num++;
        process.instrucnum=task.instrucnum;
        process.instruc_list=task.instruc_list;
        process.size=task.size;
        process.data_size=task.data_size;
        process.syn_flag=task.syn_flag;
        process.resource_flag=task.resource_flag;
        process.all_resources=task.all_resources;
        process.need_resources=task.need_resources;
        process.alloctate_flag=0;
        process.stack=new Stack();
        process.page_num = process.size % 512 == 0 ? process.size / 512 : process.size / 512 + 1;

        //求出为应进程分配的页面数量
        process.pages = new int[process.page_num];
        for(int i=0;i<process.page_num;i++)
            process.pages[i]=-1;
        memory_manage(process,1);//为进程分配页面

    }


    public void destroy(){//进程撤销原语

        memory_manage(q1.peek(),3);//收回该进程所占的内存空间

        q1.peek().outtime=computer.clock.gettime();//记录进程撤销时间

        q1.peek().ProState=5;//进程状态变为已完成



        System.out.print(computer.clock.gettime()+"时刻|"+q1.peek().ProID+"号进程已完成,撤销|回收页面号:");

        log.print(computer.clock.gettime()+"时刻|"+q1.peek().ProID+"号进程已完成,撤销|回收页面号:");
        txt.append(computer.clock.gettime()+"时刻|"+q1.peek().ProID+"号进程已完成,撤销|回收页面号:");

        for(int i=0;i<q1.peek().page_num;i++) {

            System.out.print(q1.peek().pages[i] + " ");

            log.print(q1.peek().pages[i] + " ");
            txt.append(q1.peek().pages[i] + " ");

        }

        if(q1.peek().resource_flag){//如果该进程占用了资源

            recycle(q1.peek());//回收进程占用资源

            System.out.print("|回收资源数:A"+q1.peek().all_resources[0]+",B"+q1.peek().all_resources[1]+",C"+

                    q1.peek().all_resources[2]+",D"+q1.peek().all_resources[3]+",E"+q1.peek().all_resources[4]);

            log.print("|回收资源数:A"+q1.peek().all_resources[0]+",B"+q1.peek().all_resources[1]+",C"+

                    q1.peek().all_resources[2]+",D"+q1.peek().all_resources[3]+",E"+q1.peek().all_resources[4]);
            txt.append("|回收资源数:A"+q1.peek().all_resources[0]+",B"+q1.peek().all_resources[1]+",C"+

                    q1.peek().all_resources[2]+",D"+q1.peek().all_resources[3]+",E"+q1.peek().all_resources[4]);
            resource_area.append(computer.clock.gettime()+"时刻|"+q1.peek().ProID+"号进程已完成,撤销"+"|回收资源数:A"+
                    q1.peek().all_resources[0]+",B"+q1.peek().all_resources[1]+",C"+ q1.peek().all_resources[2]+
                    ",D"+q1.peek().all_resources[3]+",E"+q1.peek().all_resources[4]);

        }

        System.out.println();

        log.println();



        q4.offer(q1.poll());//将该进程放入已完成队列

        if(q5.size()!=0){//若有进程尚未分配页面

            memory_manage(q5.poll(),1);

        }

        if(q2.size()!=0){

            q1.offer(q2.poll());

            q1.peek().ProState=1;//进程变为运行态

            computer.cpu.PC=q1.peek().PSW;

            if(computer.cpu.PC==q1.peek().instrucnum){//进程已执行完所有指令

                destroy();//撤销进程

            }else {

                memory_manage(q1.peek(), 2);

            }

        }

    }


    public void lock_destroy(Process process){//因为造成死锁而撤销进程

        memory_manage(process,3);//收回该进程所占的内存空间

        process.outtime=computer.clock.gettime();//记录进程撤销时间

        process.ProState=6;//进程状态变为已撤销



        System.out.print(computer.clock.gettime()+"时刻|因产生死锁撤销"+process.ProID+"号进程|回收页面号:");

        log.print(computer.clock.gettime()+"时刻|因产生死锁撤销"+process.ProID+"号进程|回收页面号:");
        deadLoc_area.append(computer.clock.gettime()+"时刻|因产生死锁撤销"+process.ProID+"号进程");
        txt.append(computer.clock.gettime()+"时刻|因产生死锁撤销"+process.ProID+"号进程|回收页面号:");
        for(int i=0;i<process.page_num;i++) {

            System.out.print(process.pages[i] + " ");
            log.print(process.pages[i] + " ");
            txt.append(process.pages[i] + " ");
        }

        recycle(process);//回收进程占用资源

        System.out.print("|回收资源数:A"+(process.all_resources[0]-process.need_resources[0])+",B"+(process.all_resources[1]-process.need_resources[1])+

                ",C"+(process.all_resources[2]-process.need_resources[2])+",D"+(process.all_resources[3]-process.need_resources[3])+

                ",E"+(process.all_resources[4]-process.need_resources[4]));

        log.print("|回收资源数:A"+(process.all_resources[0]-process.need_resources[0])+",B"+(process.all_resources[1]-process.need_resources[1])+

                ",C"+(process.all_resources[2]-process.need_resources[2])+",D"+(process.all_resources[3]-process.need_resources[3])+

                ",E"+(process.all_resources[4]-process.need_resources[4]));
        deadLoc_area.append("|回收资源数:A"+(process.all_resources[0]-process.need_resources[0])+",B"+(process.all_resources[1]-process.need_resources[1])+

                ",C"+(process.all_resources[2]-process.need_resources[2])+",D"+(process.all_resources[3]-process.need_resources[3])+

                ",E"+(process.all_resources[4]-process.need_resources[4])+"\n");
        txt.append("|回收资源数:A"+(process.all_resources[0]-process.need_resources[0])+",B"+(process.all_resources[1]-process.need_resources[1])+

                ",C"+(process.all_resources[2]-process.need_resources[2])+",D"+(process.all_resources[3]-process.need_resources[3])+

                ",E"+(process.all_resources[4]-process.need_resources[4])+"\n");

        System.out.println();

        log.println();

        /*if(pv_flag==process.ProID) {
            pv_flag = -1;
            mutex_area.append(computer.clock.gettime()+"时刻|"+process.ProID+"号进程释放互斥信号量\n");
            if(q3.size()!=0)
                wake();
        }*/
        q6.offer(process);//进程加入已撤销队列

    }


    public void block(Process process){//进程阻塞原语

        q3.offer(process);//将该进程放入阻塞队列

    }


    public void wake(){//进程唤醒原语

        q3.peek().ProState=2;//进程变为就绪态

        q2.offer(q3.poll());

    }


    boolean allocate(Process process){//为进程分配资源，若资源足够则分配成功，返回true，资源不够则分配失败，返回false

        if(process.alloctate_flag==0){//如果进程尚未分配过资源
            System.out.print(computer.clock.gettime()+"时刻|"+process.ProID+"号进程申请资源:");
            log.print(computer.clock.gettime()+"时刻|"+process.ProID+"号进程申请资源:");
            resource_area.append(computer.clock.gettime()+"时刻|"+process.ProID+"号进程申请资源:");
            System.out.print("A"+process.need_resources[0]/2+",B"+process.need_resources[1]/2+",C"+process.need_resources[2]/2+
                    ",D"+process.need_resources[3]/2+ ",E"+process.need_resources[4]/2);
            log.print("A"+process.need_resources[0]/2+",B"+process.need_resources[1]/2+",C"+process.need_resources[2]/2+
                    ",D"+process.need_resources[3]/2+ ",E"+process.need_resources[4]/2);
            resource_area.append("A"+process.need_resources[0]/2+",B"+process.need_resources[1]/2+",C"+process.need_resources[2]/2+
                    ",D"+process.need_resources[3]/2+ ",E"+process.need_resources[4]/2);
            System.out.println();
            log.println();
            resource_area.append("\n");

            System.out.print(computer.clock.gettime()+"时刻|为"+process.ProID+"号进程分配资源:");
            log.print(computer.clock.gettime()+"时刻|为"+process.ProID+"号进程分配资源:");
            resource_area.append(computer.clock.gettime()+"时刻|为"+process.ProID+"号进程分配资源:");


            for(int i=0;i<5;i++){
                int n=process.need_resources[i]/2;
                if(resource_num[i]>=n){//如果剩余资源数量足够
                    //分配资源
                    int num=0;
                    for(int j=0;j<5;j++){
                        if(num==n) break;
                        if(resources[i][j]==-1){
                            resources[i][j]=process.ProID;
                            num++;
                        }
                    }

                    resource_num[i]-=n;
                    process.need_resources[i]-=n;
                    System.out.print((char)(i+65)+":"+n+" ");
                    log.print((char)(i+65)+":"+n+" ");
                    resource_area.append((char)(i+65)+":"+n+" ");

                }else{//如果剩余资源不够
                    System.out.println();
                    log.println();
                    resource_area.append("\n");
                    System.out.println("系统资源不足,"+process.ProID+"号进程阻塞");
                    log.println("系统资源不足,"+process.ProID+"号进程阻塞");
                    resource_area.append("系统资源不足,"+process.ProID+"号进程阻塞\n");
                    return false;
                }

            }

            //资源分配成功
            System.out.println();
            log.println();
            resource_area.append("\n");
            process.alloctate_flag=1;

            return true;

        }else{//如果进程已经分配过资源

            System.out.print(computer.clock.gettime()+"时刻|"+process.ProID+"号进程申请资源:");
            log.print(computer.clock.gettime()+"时刻|"+process.ProID+"号进程申请资源:");
            resource_area.append(computer.clock.gettime()+"时刻|"+process.ProID+"号进程申请资源:");
            System.out.print("A"+process.need_resources[0]/2+",B"+process.need_resources[1]/2+",C"+process.need_resources[2]/2+
                    ",D"+process.need_resources[3]/2+ ",E"+process.need_resources[4]/2);
            log.print("A"+process.need_resources[0]/2+",B"+process.need_resources[1]/2+",C"+process.need_resources[2]/2+
                    ",D"+process.need_resources[3]/2+ ",E"+process.need_resources[4]/2);
            resource_area.append("A"+process.need_resources[0]/2+",B"+process.need_resources[1]/2+",C"+process.need_resources[2]/2+
                    ",D"+process.need_resources[3]/2+ ",E"+process.need_resources[4]/2);
            System.out.println();
            log.println();
            resource_area.append("\n");

            System.out.print(computer.clock.gettime()+"时刻|为"+process.ProID+"号进程分配资源:");
            log.print(computer.clock.gettime()+"时刻|为"+process.ProID+"号进程分配资源:");
            resource_area.append(computer.clock.gettime()+"时刻|为"+process.ProID+"号进程分配资源:");

            for(int i=0;i<5;i++){

                int n=process.need_resources[i];

                if(resource_num[i]>=n){//如果剩余资源数量足够

                    //分配资源

                    int num=0;

                    for(int j=0;j<5;j++){

                        if(num==n) break;

                        if(resources[i][j]==-1){

                            resources[i][j]=process.ProID;

                            num++;

                        }

                    }

                    resource_num[i]-=n;

                    process.need_resources[i]-=n;
                    System.out.print((char)(i+65)+":"+n+" ");
                    log.print((char)(i+65)+":"+n+" ");
                    resource_area.append((char)(i+65)+":"+n+" ");

                }else{//如果剩余资源不够
                    System.out.println();
                    log.println();
                    resource_area.append("\n");
                    System.out.println("系统资源不足,"+process.ProID+"号进程阻塞");
                    log.println("系统资源不足,"+process.ProID+"号进程阻塞");
                    resource_area.append("系统资源不足,"+process.ProID+"号进程阻塞\n");
                    return false;

                }

            }

            //资源分配成功
            System.out.println();
            log.println();
            resource_area.append("\n");
            process.alloctate_flag=1;
            return true;
        }

    }


    void recycle(Process process){//回收进程所占资源

        for(int i=0;i<5;i++){

            if(process.all_resources[i]!=0){//如果进程占有了该类资源
                for(int j=0;j<5;j++){
                    if(resources[i][j]==process.ProID)//如果该资源被该进程所占用
                        resources[i][j]=-1;

                }

                resource_num[i]+=process.all_resources[i]-process.need_resources[i];

            }

        }

    }


    void deadlock(){//死锁检测与撤销

        int available[]=new int[5];//当前系统资源数量

        Vector<Process>re_process=new Vector<Process>();//需要分配资源的进程序列
        Vector<Process>list_remove=new Vector<Process>();//记录可以分配资源的队列



        for(Process process:q_re) {
            if (allocate(process)) {//如果系统资源足够分配
                list_remove.add(process);//记录可以分配资源的进程
            }
        }

        q_re.removeAll(list_remove);//将能分配资源的进程从等待分配资源队列中移出
        for(Process process:list_remove){
            process.ProState=2;//进程状态变为就绪态
            q2.offer(process);//进程加入就绪队列
        }

        list_remove.clear();



        for(int i=0;i<5;i++)//记录当前系统的资源数量
            available[i]=resource_num[i];



        for(Process process:q1)
            if(process.resource_flag)re_process.add(process);
        for(Process process:q2)
            if(process.resource_flag)re_process.add(process);
        for(Process process:q3)
            if(process.resource_flag)re_process.add(process);

        for(Process process:q_re)
            re_process.add(process);



        //银行家算法

        boolean satisfy_flag = false;//记录是否有满足的进程
        list_remove.clear();
        for (; ; ) {
            satisfy_flag = false;
            for (Process process : re_process) {
                boolean flag = true;
                for (int i = 0; i < 5; i++) {
                    if (available[i] < process.need_resources[i]) {
                        flag = false;
                        break;
                    }
                }

                if (flag == true) {//如果系统资源能满足该进程
                    for (int i = 0; i < 5; i++) {
                        available[i] += process.all_resources[i] - process.need_resources[i];//回收该进程已占有的资源
                    }
                    satisfy_flag = true;
                    list_remove.add(process);//该进程可以完成
                }
            }

            if (satisfy_flag == false) break;//如果本次循环没有能满足的进程则中断
            re_process.removeAll(list_remove);//移除可以完成的进程
            list_remove.clear();

        }

        if(!re_process.isEmpty()){//还有无法分配资源的进程，即存在死锁
            Vector<Integer>nums=new Vector<Integer>();//储存需要撤销的进程序号
            for(Process process:re_process)
                nums.add(process.ProID);

            //撤销产生死锁的进程
            list_remove.clear();
            for(Process process:q3){
                if(nums.contains(process.ProID)){//如果该进程需要撤销
                    list_remove.add(process);//记录需要撤销的进程
                }
            }

            q3.removeAll(list_remove);//撤销产生死锁的进程
            for(Process process:list_remove)
                lock_destroy(process);


            list_remove.clear();

            for(Process process:q2){
                if(nums.contains(process.ProID)){//如果该进程需要撤销
                    list_remove.add(process);//记录需要撤销的进程
                }
            }

            q2.removeAll(list_remove);//撤销产生死锁的进程
            for(Process process:list_remove) {
                if(pv_flag==process.ProID){
                    pv_flag=-1;
                    if(!q3.isEmpty())
                        wake();
                }
                lock_destroy(process);
            }

            list_remove.clear();

            for(Process process:q1){
                if(nums.contains(process.ProID)){//如果该进程需要撤销
                    list_remove.add(process);//记录需要撤销的进程
                }
            }

            q1.removeAll(list_remove);//撤销产生死锁的进程
            for(Process process:list_remove) {
                if(pv_flag==process.ProID){
                    pv_flag=-1;
                    if(!q3.isEmpty())
                        wake();
                }
                lock_destroy(process);
            }

            list_remove.clear();

            for(Process process:q_re){
                if(nums.contains(process.ProID)){//如果该进程需要撤销
                    list_remove.add(process);//记录需要撤销的进程
                }
            }
            q_re.removeAll(list_remove);//撤销产生死锁的进程
            for(Process process:list_remove)
                lock_destroy(process);
        }

    }


    void page_in(int page){//缺页中断，将页面号为page的页面装入，采用LRU算法
        System.out.print(computer.clock.gettime()+"时刻|发生缺页中断,");
        log.print(computer.clock.gettime()+"时刻|发生缺页中断,");
        page_short_txt.append(computer.clock.gettime()+"时刻|发生缺页中断,");

        boolean flag=false;

        for(int i=0;i<64;i++){//寻找空闲的页框

            if(!memory_table[i]) {//该页框空闲

                page_table[page][0]=(byte)(page_table[page][0]|64);

                //将页表项的驻留标志位改为1

                page_table[page][0]=(byte)(page_table[page][0]&192);

                page_table[page][0]+=(byte)i;

                //将页面所分得页框的页框号写入该页表项的2-7位

                memory_table[i]=true;//表示该页框已被占用

                flag=true;
                System.out.println("将"+page+"号页面装入"+i+"号页框");
                log.println("将"+page+"号页面装入"+i+"号页框");
                page_short_txt.append("将"+page+"号页面装入"+i+"号页框\n");
                break;

            }

        }

        if(flag==false){//没有空闲页框，则要替换已分配的页框，采用LRU算法，即选出计数器最小的一个页面替换之
            int min=0;
            int add_min=-1;
            for(int i=0;i<128;i++){//找出最近最少使用的页面
                if((page_table[i][1]&128)==128) continue;//跳过受保护页面
                if((page_table[i][0]&64)==0) continue;//跳过没有分配页框的页面
                if(add_min==-1){
                    min=(page_table[i][1]&127);
                    add_min=i;
                }

                else if((page_table[i][1]&127)<min){
                    min=(page_table[i][1]&127);
                    add_min=i;
                }
            }

            int i=page_table[add_min][0]&63;//求出要替换掉的页面所占的页框号
            System.out.println("没有空闲页框,根据LRU算法替换掉"+add_min+"号页面,将"+page+"号界面装入"+i+"号页框");
            log.println("没有空闲页框,根据LRU算法替换掉"+add_min+"号页面,将"+page+"号界面装入"+i+"号页框");
            page_short_txt.append("没有空闲页框,根据LRU算法替换掉"+add_min+"号页面,将"+page+"号界面装入"+i+"号页框\n");

            page_table[add_min][0]=(byte)(page_table[add_min][0]&191);//将要替换的页面的驻留标志位改为0
            page_table[page][0]=(byte)(page_table[page][0]|64);
            //将要装入页面的页表项的驻留标志位改为1
            page_table[page][0]=(byte)(page_table[page][0]&192);
            page_table[page][0]+=(byte)i;
            //将页面所分得页框的页框号写入该页表项的2-7位

        }

    }


    void page_use_move(){//每过1000ms将每个在内存中页面的引用计数器右移1位，在cpu_manage中调用

        for(int i=0;i<128;i++){

            if((page_table[i][0]&64)==0) continue;//跳过没有分配页框的页面
            byte flag=(byte)(page_table[i][1]&128);//保存该页面的保护位
            page_table[i][1]=(byte)(page_table[i][1]>>>1);
            page_table[i][1]=(byte)(page_table[i][1]&63);
            page_table[i][1]=(byte)(page_table[i][1]&flag);//还原保护位
        }
    }


    void gui(){//可视化显示

        show_memory();
        txt.append("");
        final TableModel tableModel=table.getModel();
        /*Queue<Process>temp;*/
        System.out.print(computer.clock.gettime()+"时刻|运行队列:");
        log.print(computer.clock.gettime()+"时刻|运行队列:");
        txt.append(computer.clock.gettime()+"时刻|运行队列:");
        for(Process process:q1){

            tableModel.setValueAt("运行态",process.ProID,2);

            if(process.syn_flag<0){
                tableModel.setValueAt("不需要",process.ProID,3);
            }else{
                tableModel.setValueAt("需要和"+process.syn_flag+"号进程同步",process.ProID,3);
            }

            if(process.resource_flag==false){
                tableModel.setValueAt("不需要",process.ProID,4);
                tableModel.setValueAt(0,process.ProID,5);
                tableModel.setValueAt(0,process.ProID,6);
            }else{
                tableModel.setValueAt("需要",process.ProID,4);
                tableModel.setValueAt("A:"+process.all_resources[0]+" B:"+process.all_resources[1]+" C:"+process.all_resources[2]
                            +"D:"+process.all_resources[3]+"E:"+process.all_resources[4],process.ProID,5);
                tableModel.setValueAt("A:"+(process.all_resources[0]-process.need_resources[0])+" B:"+
                        (process.all_resources[1]-process.need_resources[1])+" C:"+
                        (process.all_resources[2]-process.need_resources[2]) +"D:"+
                        (process.all_resources[3]-process.need_resources[3])+"E:"+
                        (process.all_resources[4]-process.need_resources[4]),process.ProID,6);
            }

            System.out.print(process.ProID+" ");
            log.print(process.ProID+" ");
            txt.append(process.ProID+" ");
        }

        /*for(int i=0;i<temp.size();i++)

            System.out.print(temp.poll().ProID+" ");*/

        System.out.print("|就绪队列:");
        log.print("|就绪队列:");
        txt.append("|就绪队列:");
        for(Process process:q2){

            tableModel.setValueAt("就绪态",process.ProID,2);


            if(process.syn_flag<0){
                tableModel.setValueAt("不需要",process.ProID,3);
            }else{
                tableModel.setValueAt("需要和"+process.syn_flag+"号进程同步",process.ProID,3);
            }

            if(process.resource_flag==false){
                tableModel.setValueAt("不需要",process.ProID,4);
                tableModel.setValueAt(0,process.ProID,5);
                tableModel.setValueAt(0,process.ProID,6);
            }else{
                tableModel.setValueAt("需要",process.ProID,4);
                tableModel.setValueAt("A:"+process.all_resources[0]+" B:"+process.all_resources[1]+" C:"+process.all_resources[2]
                        +"D:"+process.all_resources[3]+"E:"+process.all_resources[4],process.ProID,5);
                tableModel.setValueAt("A:"+(process.all_resources[0]-process.need_resources[0])+" B:"+
                        (process.all_resources[1]-process.need_resources[1])+" C:"+
                        (process.all_resources[2]-process.need_resources[2]) +"D:"+
                        (process.all_resources[3]-process.need_resources[3])+"E:"+
                        (process.all_resources[4]-process.need_resources[4]),process.ProID,6);
            }
            System.out.print(process.ProID+" ");
            log.print(process.ProID+" ");
            txt.append(process.ProID+" ");

        }



        System.out.print("|阻塞队列:");

        log.print("|阻塞队列:");
        txt.append("|阻塞队列:");
        for(Process process:q3){
            tableModel.setValueAt("阻塞态",process.ProID,2);


            if(process.syn_flag<0){
                tableModel.setValueAt("不需要",process.ProID,3);
            }else{
                tableModel.setValueAt("需要和"+process.syn_flag+"号进程同步",process.ProID,3);
            }

            if(process.resource_flag==false){
                tableModel.setValueAt("不需要",process.ProID,4);
                tableModel.setValueAt(0,process.ProID,5);
                tableModel.setValueAt(0,process.ProID,6);
            }else{
                tableModel.setValueAt("需要",process.ProID,4);
                tableModel.setValueAt("A:"+process.all_resources[0]+" B:"+process.all_resources[1]+" C:"+process.all_resources[2]
                        +"D:"+process.all_resources[3]+"E:"+process.all_resources[4],process.ProID,5);
                tableModel.setValueAt("A:"+(process.all_resources[0]-process.need_resources[0])+" B:"+
                        (process.all_resources[1]-process.need_resources[1])+" C:"+
                        (process.all_resources[2]-process.need_resources[2]) +"D:"+
                        (process.all_resources[3]-process.need_resources[3])+"E:"+
                        (process.all_resources[4]-process.need_resources[4]),process.ProID,6);
            }
            System.out.print(process.ProID+" ");
            log.print(process.ProID+" ");
            txt.append(process.ProID+" ");
        }
        /*temp=q3;

        for(int i=0;i<temp.size();i++)

            System.out.print(temp.poll().ProID+" ");*/
        System.out.print("|已完成进程:");
        log.print("|已完成进程:");
        txt.append("|已完成进程:");
        for(Process process:q4){

            tableModel.setValueAt("已完成",process.ProID,2);

            if(process.resource_flag==false){
                tableModel.setValueAt("不需要",process.ProID,4);
                tableModel.setValueAt(0,process.ProID,5);
                tableModel.setValueAt(0,process.ProID,6);
            }else{
                tableModel.setValueAt("需要",process.ProID,4);
                tableModel.setValueAt("A:"+process.all_resources[0]+" B:"+process.all_resources[1]+" C:"+process.all_resources[2]
                        +"D:"+process.all_resources[3]+"E:"+process.all_resources[4],process.ProID,5);
                tableModel.setValueAt("A:"+(process.all_resources[0]-process.need_resources[0])+" B:"+
                        (process.all_resources[1]-process.need_resources[1])+" C:"+
                        (process.all_resources[2]-process.need_resources[2]) +"D:"+
                        (process.all_resources[3]-process.need_resources[3])+"E:"+
                        (process.all_resources[4]-process.need_resources[4]),process.ProID,6);
            }

            System.out.print(process.ProID+" ");
            log.print(process.ProID+" ");
            txt.append(process.ProID+" ");
        }

        /*temp=q4;

        for(int i=0;i<temp.size();i++)

            System.out.print(temp.poll().ProID+" ");*/
        System.out.print("|等待空闲页面进程:");
        log.print("|等待空闲页面进程:");
        txt.append("|等待空闲页面进程:");
        for(Process process:q5){
            tableModel.setValueAt("等待分配页面",process.ProID,2);

            if(process.syn_flag<0){
                tableModel.setValueAt("不需要",process.ProID,3);
            }else{
                tableModel.setValueAt("需要和"+process.syn_flag+"号进程同步",process.ProID,3);
            }

            if(process.resource_flag==false){
                tableModel.setValueAt("不需要",process.ProID,4);
                tableModel.setValueAt(0,process.ProID,5);
                tableModel.setValueAt(0,process.ProID,6);
            }else{
                tableModel.setValueAt("需要",process.ProID,4);
                tableModel.setValueAt("A:"+process.all_resources[0]+" B:"+process.all_resources[1]+" C:"+process.all_resources[2]
                        +"D:"+process.all_resources[3]+"E:"+process.all_resources[4],process.ProID,5);
                tableModel.setValueAt("A:"+(process.all_resources[0]-process.need_resources[0])+" B:"+
                        (process.all_resources[1]-process.need_resources[1])+" C:"+
                        (process.all_resources[2]-process.need_resources[2]) +"D:"+
                        (process.all_resources[3]-process.need_resources[3])+"E:"+
                        (process.all_resources[4]-process.need_resources[4]),process.ProID,6);
            }
            System.out.print(process.ProID+" ");
            log.print(process.ProID+" ");
            txt.append(process.ProID+" ");
        }



        System.out.print("|已撤销进程:");
        log.print("|已撤销进程:");
        txt.append("|已撤销进程:");
        for(Process process:q6){


            tableModel.setValueAt("已撤销",process.ProID,2);

            if(process.syn_flag<0){
                tableModel.setValueAt("不需要",process.ProID,3);
            }else{
                tableModel.setValueAt("需要和"+process.syn_flag+"号进程同步",process.ProID,3);
            }


            tableModel.setValueAt("需要",process.ProID,4);
            tableModel.setValueAt("A:"+process.all_resources[0]+" B:"+process.all_resources[1]+" C:"+process.all_resources[2]
                    +"D:"+process.all_resources[3]+"E:"+process.all_resources[4],process.ProID,5);
            tableModel.setValueAt("A:0 B:0 C:0 D:0 E:0",process.ProID,6);
            System.out.print(process.ProID+" ");
            log.print(process.ProID+" ");
            txt.append(process.ProID+" ");

        }



        System.out.print("|等待分配资源进程:");
        log.print("|等待分配资源进程:");
        txt.append("|等待分配资源进程:");
        for(Process process:q_re){
            tableModel.setValueAt("阻塞态",process.ProID,2);

            if(process.syn_flag<0){
                tableModel.setValueAt("不需要",process.ProID,3);
            }else{
                tableModel.setValueAt("需要和"+process.syn_flag+"号进程同步",process.ProID,3);
            }

            tableModel.setValueAt("需要",process.ProID,4);
            tableModel.setValueAt("A:"+process.all_resources[0]+" B:"+process.all_resources[1]+" C:"+process.all_resources[2]
                    +"D:"+process.all_resources[3]+"E:"+process.all_resources[4],process.ProID,5);
            tableModel.setValueAt("A:"+(process.all_resources[0]-process.need_resources[0])+" B:"+
                    (process.all_resources[1]-process.need_resources[1])+" C:"+
                    (process.all_resources[2]-process.need_resources[2]) +"D:"+
                    (process.all_resources[3]-process.need_resources[3])+"E:"+
                    (process.all_resources[4]-process.need_resources[4]),process.ProID,6);

            System.out.print(process.ProID+" ");
            log.print(process.ProID+" ");
            txt.append(process.ProID+" ");

        }


        System.out.println("");
        log.println("");
        txt.append("\n");

        resource_field.setText("A:"+resource_num[0]+" | B:"+resource_num[1]+" | C:"+resource_num[2]+" | D:"+resource_num[3]+" | E:"+resource_num[4]);

        int num=0;
        for(int i=0;i<64;i++){
            if(memory_table[i])
                num++;
        }
        memory_field.setText("已占用"+((double)(num*512)/1024.0)+"KB/32KB");

        if(q1.size()==0) {
            cpu_field.setText("空闲");
            instr_field.setText("");
        }
        else
            cpu_field.setText(q1.peek().ProID+"号进程");

    }


    void showjobs(){
        System.out.println("共有"+job_num+"个作业");
        log.println("共有"+job_num+"个作业");

        txt.append("共有"+job_num+"个作业\n");
        int n=0;
        for(int i=0;i<job_num;i++){
            for(int j=0;j<jobs[i].task_num;j++)
                n++;
        }
        System.out.println("共有"+n+"个进程");
        log.println("共有"+n+"个进程");
        txt.append("共有"+n+"个进程\n");
        for(int i=0;i<job_num;i++){
            System.out.println("作业"+(i+1)+":  intime"+jobs[i].intime);
            log.println("作业"+(i+1)+":  intime"+jobs[i].intime);
            txt.append("作业"+(i+1)+":  intime"+jobs[i].intime+"\n");
            for(int j=0;j<jobs[i].task_num;j++){
                System.out.print("进程"+(j+1)+":  指令数目"+jobs[i].task_list[j].instrucnum
                        +"  所需内存"+jobs[i].task_list[j].size);
                log.print("进程"+(j+1)+":  指令数目"+jobs[i].task_list[j].instrucnum
                        +"  所需内存"+jobs[i].task_list[j].size);
                txt.append("进程"+(j+1)+":  指令数目"+jobs[i].task_list[j].instrucnum
                        +"  所需内存"+jobs[i].task_list[j].size);
                if(jobs[i].task_list[j].resource_flag){
                    System.out.println("  所需资源"+jobs[i].task_list[j].all_resources[0]+jobs[i].task_list[j].all_resources[1]+
                            jobs[i].task_list[j].all_resources[2]+jobs[i].task_list[j].all_resources[3]+jobs[i].task_list[j].all_resources[4]);
                    log.println("  所需资源"+jobs[i].task_list[j].all_resources[0]+jobs[i].task_list[j].all_resources[1]+
                            jobs[i].task_list[j].all_resources[2]+jobs[i].task_list[j].all_resources[3]+jobs[i].task_list[j].all_resources[4]);
                    txt.append("  所需资源"+jobs[i].task_list[j].all_resources[0]+jobs[i].task_list[j].all_resources[1]+
                            jobs[i].task_list[j].all_resources[2]+jobs[i].task_list[j].all_resources[3]+jobs[i].task_list[j].all_resources[4]+"\n");
                }else{
                    System.out.println();
                    log.println();
                    txt.append("\n");
                }
                for(int k=0;k<jobs[i].task_list[j].instrucnum;k++){
                    System.out.println("ID"+jobs[i].task_list[j].instruc_list[k].Instruc_ID
                            +"  类型"+jobs[i].task_list[j].instruc_list[k].Instruc_State+"  运行时间"
                            +jobs[i].task_list[j].instruc_list[k].Instruct_Times+"  访问数据"
                            +jobs[i].task_list[j].instruc_list[k].data_flag);
                    log.println("ID"+jobs[i].task_list[j].instruc_list[k].Instruc_ID
                            +"  类型"+jobs[i].task_list[j].instruc_list[k].Instruc_State+"  运行时间"
                            +jobs[i].task_list[j].instruc_list[k].Instruct_Times+"  访问数据"
                            +jobs[i].task_list[j].instruc_list[k].data_flag);
                    txt.append("ID"+jobs[i].task_list[j].instruc_list[k].Instruc_ID
                            +"  类型"+jobs[i].task_list[j].instruc_list[k].Instruc_State+"  运行时间"
                            +jobs[i].task_list[j].instruc_list[k].Instruct_Times+"  访问数据"
                            +jobs[i].task_list[j].instruc_list[k].data_flag+"\n");
                }
            }
        }
    }


    void show_memory(){
        final TableModel memoryTabelModel=memoryTabel.getModel();
        for(int i=0;i<64;i++){
            memoryTabelModel.setValueAt("空闲",i,1);
            memoryTabelModel.setValueAt("空闲",i,2);
        }

        System.out.println(computer.clock.gettime() + "时刻|各进程占用内存情况");
        //time_field.setText(computer.clock.gettime()+"毫秒");
        time_field2.setText(computer.clock.gettime()+"毫秒");
        //textArea.append(computer.clock.gettime() + "时刻|各进程占用内存情况\n");
        memory_log.println(computer.clock.gettime() + "时刻|各进程占用情况");
        System.out.println("{");
        memory_log.println("{");
       // textArea.append("{\n");

        for (Process process : q1) {
            System.out.print(process.ProID + "号进程分配页面号:");
            memory_log.print(process.ProID + "号进程分配页面号:");
            for (int page : process.pages) {
                System.out.print(page);
                memory_log.print(page);

                if ((page_table[page][0] & 64) == 0) {
                    System.out.print("(不在内存)");
                    memory_log.print("(不在内存)");

                } else {
                    int i = page_table[page][0] & 63;
                    System.out.print("(页框号" + i + ")");
                    memory_log.print("(页框号" + i + ")");
                    memoryTabelModel.setValueAt(process.ProID+"号进程已占用",i,1);
                    memoryTabelModel.setValueAt(page+"号页面",i,2);
                }

                System.out.print("  ");
                memory_log.print("  ");
                //textArea.append("  ");
            }



            System.out.println();
            memory_log.println();
           // textArea.append("\n");

        }



        for (Process process : q2) {
            System.out.print(process.ProID + "号进程分配页面号:");
            memory_log.print(process.ProID + "号进程分配页面号:");
           // textArea.append(process.ProID + "号进程分配页面号:");
            for (int page : process.pages) {
                System.out.print(page);
                memory_log.print(page);
                //textArea.append(""+page+"");
                if ((page_table[page][0] & 64) == 0) {
                    System.out.print("(不在内存)");
                    memory_log.print("(不在内存)");
                   // textArea.append("(不在内存)");
                } else {
                    int i = page_table[page][0] & 63;

                    System.out.print("(页框号" + i + ")");
                    memory_log.print("(页框号" + i + ")");
                    memoryTabelModel.setValueAt(process.ProID+"号进程已占用",i,1);
                    memoryTabelModel.setValueAt(page+"号页面",i,2);

                }

                System.out.print("  ");
                memory_log.print("  ");
                //textArea.append("  ");

            }



            System.out.println();
            memory_log.println();
            //textArea.append("\n");
        }



        for (Process process : q3) {
            System.out.print(process.ProID + "号进程分配页面号:");
            memory_log.print(process.ProID + "号进程分配页面号:");
            //textArea.append(process.ProID + "号进程分配页面号:");
            for (int page : process.pages) {
                System.out.print(page);
                memory_log.print(page);
               // textArea.append(""+page+"");
                if ((page_table[page][0] & 64) == 0) {
                    System.out.print("(不在内存)");
                    memory_log.print("(不在内存)");
                    //textArea.append("(不在内存)");
                } else {
                    int i = page_table[page][0] & 63;
                    System.out.print("(页框号" + i + ")");
                    memory_log.print("(页框号" + i + ")");
                    memoryTabelModel.setValueAt(process.ProID+"号进程已占用",i,1);
                    memoryTabelModel.setValueAt(page+"号页面",i,2);

                }

                System.out.print("  ");
                memory_log.print("  ");


            }



            System.out.println();
            memory_log.println();


        }

        for (Process process : q_re) {
            System.out.print(process.ProID + "号进程分配页面号:");
            memory_log.print(process.ProID + "号进程分配页面号:");
            for (int page : process.pages) {
                System.out.print(page);
                memory_log.print(page);

                if ((page_table[page][0] & 64) == 0) {
                    System.out.print("(不在内存)");
                    memory_log.print("(不在内存)");

                } else {
                    int i = page_table[page][0] & 63;
                    System.out.print("(页框号" + i + ")");
                    memory_log.print("(页框号" + i + ")");
                    memoryTabelModel.setValueAt(process.ProID+"号进程已占用",i,1);
                    memoryTabelModel.setValueAt(page+"号页面",i,2);
                }

                System.out.print("  ");
                memory_log.print("  ");
                //textArea.append("  ");
            }



            System.out.println();
            memory_log.println();
            // textArea.append("\n");

        }

        System.out.println("}");
        memory_log.println("}");

    }

    void read_pcb(File pcb_file){//读入JCB、PCB信息
        try {
            FileReader in=new FileReader(pcb_file);
            BufferedReader pcb=new BufferedReader(in);
            string_int(pcb.readLine());//提取作业数量
            job_num=num_temp.poll();

            jobs=new Job[job_num];//创建作业序列
            for(int i=0;i<job_num;i++)
                jobs[i]=new Job();

            for(int i=0;i<job_num;i++){
                string_int(pcb.readLine());
                num_temp.poll();//移除作业序号
                jobs[i].intime=num_temp.poll();//求出作业进入系统时间
                jobs[i].task_num=num_temp.poll();//求出作业包含进程总数

                jobs[i].task_list=new Task[jobs[i].task_num];//创建作业包含的进程序列
                for(int j=0;j<jobs[i].task_num;j++)
                    jobs[i].task_list[j]=new Task();

                for(int j=0;j<jobs[i].task_num;j++){
                    string_int(pcb.readLine());
                    int num=num_temp.poll();//记录进程序号
                    jobs[i].task_list[j].instrucnum=num_temp.poll();//进程包含指令数目
                    jobs[i].task_list[j].data_size=num_temp.poll();//进程数据区大小
                    jobs[i].task_list[j].size=num_temp.poll();//进程所需内存总大小
                    if(num_temp.peek()==-1){//如果进程不需要资源
                        jobs[i].task_list[j].resource_flag=false;
                        num_temp.poll();
                    }else{//如果进程需要资源
                        jobs[i].task_list[j].resource_flag=true;
                        jobs[i].task_list[j].all_resources=new int[5];
                        jobs[i].task_list[j].need_resources=new int[5];
                        for(int k=0;k<5;k++){
                            jobs[i].task_list[j].all_resources[k]=num_temp.poll();
                            jobs[i].task_list[j].need_resources[k]=jobs[i].task_list[j].all_resources[k];
                        }
                    }
                    jobs[i].task_list[j].syn_flag=num_temp.poll();//进程的同步标志
                    if(jobs[i].task_list[j].syn_flag!=-1&&syn_flag==-1)
                        syn_flag=num;

                    jobs[i].task_list[j].instruc_list=new Instruct[jobs[i].task_list[j].instrucnum];//创建进程包含的指令序列

                    for(int k=0;k<jobs[i].task_list[j].instrucnum;k++){
                        string_int(pcb.readLine());
                        Instruct instruct=new Instruct();
                        instruct.Instruc_ID=num_temp.poll();//指令序号
                        instruct.Instruc_State=num_temp.poll();//指令类型
                        instruct.Instruct_Times=num_temp.poll();//指令运行时间
                        instruct.needtime=instruct.Instruct_Times;
                        instruct.data_flag=num_temp.poll();//指令是否访问数据
                        jobs[i].task_list[j].instruc_list[k]=instruct;
                    }
                }
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    void wtite_pcb(){//向文件中写入JCB、PCB信息
        File directory=new File(".");
        String path=null;
        int pro_num=0;//记录进程序号
        try{
            path=directory.getCanonicalPath();
        }catch(IOException e){
            e.printStackTrace();
        }
        path+="\\pcbs_save.txt";//将生成的进程信息存储在pcbs_save.txt中
        File pcbfile=new File(path);
        try {
            if (!pcbfile.exists())
                pcbfile.createNewFile();
            FileOutputStream out=new FileOutputStream(pcbfile);
            PrintStream pcbs=new PrintStream(out);
            pcbs.println("作业数量:"+jobs.length);
            for(int i=0;i<job_num;i++){
                pcbs.println("作业"+(i+1)+" 提交时间:"+jobs[i].intime+" 进程数量:"+jobs[i].task_num);
                for(int j=0;j<jobs[i].task_num;j++){
                    pcbs.print(" 进程序号:"+(pro_num++)+" 指令数目:"+jobs[i].task_list[j].instrucnum+" 数据区大小:"+
                            jobs[i].task_list[j].data_size+" 总大小:"+jobs[i].task_list[j].size+" 所需资源:");
                    if(jobs[i].task_list[j].resource_flag){//如果需要资源
                        for(int k=0;k<5;k++){
                            pcbs.print((char)(k+65));
                            pcbs.print(jobs[i].task_list[j].all_resources[k]);
                            if(k!=4)
                                pcbs.print(",");
                        }
                    }else{
                        pcbs.print(-1);
                    }
                    pcbs.print(" 是否同步:");
                    if(jobs[i].task_list[j].syn_flag!=-1){//如果需要同步
                        pcbs.println(jobs[i].task_list[j].syn_flag);
                    }else{
                        pcbs.println(-1);
                    }
                    for(int k=0;k<jobs[i].task_list[j].instrucnum;k++){
                        pcbs.println("  指令序号:"+jobs[i].task_list[j].instruc_list[k].Instruc_ID+" 类型:"+
                                jobs[i].task_list[j].instruc_list[k].Instruc_State+" 所需时间:"+
                                jobs[i].task_list[j].instruc_list[k].Instruct_Times+" 是否访问数据:"+
                                jobs[i].task_list[j].instruc_list[k].data_flag);
                    }
                }
            }
            pcbs.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    void string_int(String s){//将字符串中数字提取到num_temp中存起来
        boolean flag=false;//读取一个数据标志位
        int data=0;//分离的一个数据
        boolean k_flag=true;//正负标志
        for(int i=0;i<s.length();i++){
            k_flag=true;
            while((s.charAt(i)>='0')&&(s.charAt(i)<='9')){//当前字符是数据，并一直读后面的数据，只要遇到不是数字为止
                if(s.charAt(i-1)=='-') k_flag=false;//如果是负数
                flag=true;
                data*=10;
                if(k_flag)//如果是正数
                    data+=(s.charAt(i)-'0');
                else
                    data-=(s.charAt(i)-'0');
                i++;
                if(i>=s.length())break;
            }
            //如果刚刚读取了数据
            if(flag){
                num_temp.offer(data);//存储数据
                data=0;//数据清零
                flag=false;//标志位复位
            }
        }
    }

    boolean mode_run(int mode){//加在开始按钮的监听中，根据用户的选择来以随机生成或读写文件方式来启动系统
        switch (mode){
            case 1: {//随机生成作业
                job_num = 5 + (int) (Math.random() * 6);//随机生成5-10个作业
                jobs = new Job[job_num];
                for (int i = 0; i < job_num; i++)//初始化作业序列
                    jobs[i] = new Job();


                //随机生成需要进行同步的进程
                for (int i = 0; i < job_num; i++)
                    for (int j = 0; j < jobs[i].task_num; j++)
                        jobs[i].task_list[j].syn_flag = -1;
                for (int i = 0; i < job_num; i++) {
                    if (jobs[i].task_num >= 2) {
                        int pro_i = 0;
                        for (int j = 0; j < i; j++)
                            for (int k = 0; k < jobs[j].task_num; k++)
                                pro_i++;
                        jobs[i].task_list[1].syn_flag = pro_i;
                        jobs[i].task_list[0].syn_flag = pro_i + 1;
                        jobs[i].task_list[0].resource_flag = false;
                        jobs[i].task_list[1].resource_flag = false;
                        syn_flag = pro_i;
                        break;
                    }
                }
                return true;
            }
            case 2:{//从文件读入作业
                JFileChooser filechooser=new JFileChooser();
                int option=filechooser.showDialog(null,null);
                if(option==JFileChooser.APPROVE_OPTION){
                    read_pcb(filechooser.getSelectedFile());
                    return true;
                }else if(option==JFileChooser.CANCEL_OPTION){
                    return false;
                }
            }
        }
        return false;
    }
}

