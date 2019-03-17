import java.util.Stack;

public class Process {//进程类
    static int num=0;//当前生成PCB的序号，从0开始
    int ProID;//进程序号
    int ProState;//1为运行态，2为就绪态，3为阻塞态，4为等待分配页面，5为已完成，6位已撤销
    int instrucnum;//进程的指令数目
    Instruct instruc_list[];//进程包含的指令序列
    int size;//进程所需内存大小
    int data_size;//进程的数据部分所需内存大小
    int page_num;//为进程分配的页面数
    int pages[];//为进程分配的页面序列
    short PSW;//当前指令编号
    int intime;//进程创建时间
    int outtime;//进程销毁时间
    int starttime;//进程开始占据CPU时间
    public int syn_flag;//同步标志，-1表示不需要同步，非负表示需要和相应序号的进程同步
    public boolean resource_flag;//进程是否需要资源，false为否，true为是
    public int all_resources[];//进程需要的资源
    public int need_resources[];//进程还需要的资源
    public int alloctate_flag;//表示进程已经分配了几次资源
    Stack stack;//线程绑定的栈，用来进行现场保护

    /*public static Process create(Os os,Task task){//进程创建原语
        Process process=new Process();
        process.ProID=num++;
        process.instrucnum=task.instrucnum;
        process.instruc_list=task.instruc_list;
        process.stack=new Stack();
        //分配内存
        process.ProState=2;//创建好的进程为就绪态
        os.q2.offer(process);//创建好的进程进入就绪队列
        return process;
    }

    public static void destroy(Os os,Process process){//进程撤销原语
            os.q4.offer(process);//将该进程放入已完成队列
    }

    public static void block(Os os,Process process){//进程阻塞原语
        os.q3.offer(process);//将该进程放入阻塞队列
    }

    public static void wake(){//进程唤醒原语

    }*/
}
