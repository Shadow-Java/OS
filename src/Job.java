public class Job {//作业类

    static int time=0;//上一个作业的提交时间

    public int intime;//提交作业的时间

    public int task_num;//作业包含的任务数目

    public Task task_list[];//作业包含的任务集



    Job(){

        intime=time+((1+(int)(Math.random()*100))*10);//每一个作业比上一个作业晚提交10ms-1000ms，为10的倍数
        time=intime;
        task_num=1+(int)(Math.random()*5);//一个作业包含1-5个任务
        task_list=new Task[task_num];
        for(int i=0;i<task_num;i++){
            Task task=new Task();
            task_list[i]=task;
        }
    }
}



class Task{//任务类，一个作业可以分解为多个任务，每个任务创建一个进程

    public int size;//任务所需内存大小,单位是内存单元个数
    public int data_size;//任务的数据部分所需内存大小
    public int instrucnum;//任务包含的指令数目
    public Instruct instruc_list[];//任务包含的指令序列
    public int syn_flag;//同步标志，-1表示不需要同步，非负表示需要和相应序号的进程同步
    public boolean resource_flag;//进程是否需要资源，false为否，true为是
    public int all_resources[];//进程需要的资源
    public int need_resources[];//进程还需要的资源



    Task() {

        size = 0;
        instrucnum = 20 + (int) (Math.random() * 281);//任务包含20-300条指令
        instruc_list = new Instruct[instrucnum];
        for (int i = 0; i < instrucnum; i++) {
            Instruct instruct = new Instruct();
            instruct.Instruc_ID = i;//指令序号
            instruct.Instruc_State = (int) (Math.random() * 3);//每条指令类型为0或1或2，0表示系统调用，1表示用户态计算操作，2表示PV操作
            instruct.Instruct_Times = (5 + (int) (Math.random() * 6)) * 10;//每条指令运行时间为50ms-100ms，为10的倍数
            instruct.needtime = instruct.Instruct_Times;
            instruct.data_flag = (int) (Math.random() * 2);//表示指令是否要访问数据，0不访问，1访问
            size += 2;//每条指令所需内存大小为2B
            instruc_list[i] = instruct;
        }

        data_size = (256 + (int) (Math.random() * 3840)) * 2;//数据所需内存大小512B-8192B，数据单位为双字节
        size += data_size;


        resource_flag = Math.random() < 0.5 ? true : false;//进程是否需要资源，false为否，true为是
        if (resource_flag == true) {//如果需要资源
            all_resources = new int[5];
            need_resources = new int[5];
            for (int j = 0; j < 5; j++) {
                all_resources[j] = (int) (Math.random() * 4);//随机需要0-3个资源
                need_resources[j] = all_resources[j];

            }

        }

    }

}