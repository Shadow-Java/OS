public class Instruct {//指令类

    public  int Instruc_ID;//指令序号,从0开始
    public int Instruc_State;//指令的类型标志，0表示系统调用，1表示用户态计算操作，2表示PV操作
    public int Instruct_Times;//指令运行时间
    public int data_flag;//表示指令是否要访问数据，0不访问，1访问
    public int starttime;//指令开始执行时间
    public int needtime;//指令还需要执行多少时间

    public void instructToString()
    {
        switch (this.Instruc_State){
            case 0:
                System.out.println("指令序号"+this.Instruc_ID+" 指令类型：系统调用指令"+" 指令开始执行时间："+this.starttime+" 指令运行时间："+this.Instruct_Times);
            case 1:
                System.out.println("指令序号"+this.Instruc_ID+" 指令类型：用户态计算指令"+" 指令开始执行时间："+this.starttime+" 指令运行时间："+this.Instruct_Times);
            case 2:
                System.out.println("指令序号"+this.Instruc_ID+" 指令类型：PV操作指令"+" 指令开始执行时间："+this.starttime+" 指令运行时间："+this.Instruct_Times);
        }

    }
}