
import java.io.*;



public class Computer {//裸机类

    short page_table;//页表基址寄存器，存放页表基址

    public Clock clock;//时钟

    public CPU cpu;//CPU

    private BUS bus;//总线

    public MMU mmu;//存储管理部件

    private Memory memory;//内存

    public Disk disk;//硬盘
    private Os os;

    Computer(){//构造函数

        clock=new Clock();

        cpu=new CPU();

        bus=new BUS();

        mmu=new MMU();

        memory=new Memory();

        disk=new Disk();

        page_table=0;

        os=new Os(this);
    }



    public void computerstart(){//开机
        os.random_buton.setEnabled(false);
        os.read_buton.setEnabled(false);
        os.run_buton.setEnabled(false);

        os.wtite_pcb();
        os.setUI_1();

        new Thread(){//启动时钟
            public void run(){
                try {
                    clock.clockstart(os);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        }.start();
        new Thread(){//操作系统等待时钟中断进行调度

            public void run(){
                try {
                    os.osstart();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }

        }.start();

    }


    class CPU{//CPU类

        public short PC;//程序计数器

        private short IR;//指令寄存器

        private int []PSW=new int [2];//指令状态字寄存器

        private int cpu_state;//CPU状态，表示内核态，1表示用户态



        /*

        public void cpu_run(Os os){

            for(;;) {

                if (os.q1.size() != 0) {

                    PC = (short) os.q1.peek().PSW;

                    if(PC==os.q1.peek().instrucnum)

                        System.out.println(clock.gettime()+"时刻"+os.q1.peek().ProID+ "进程执行结束");

                    os.q1.peek().instruc_list[PC].starttime = clock.gettime();

                }

            }

        }*/

    }

    public static void main(String []Args){
        Computer computer=new Computer();
    }
}

class Clock{//时钟类

    private int time;//记录系统时间，以毫秒为单位



    Clock(){

        time=0;//系统时间初始为0

    }



    public void clockstart(Os os)throws InterruptedException{//每隔10毫秒系统时间加10并进行时钟中断

        for(;;) {

            Thread.sleep(10);

            synchronized(this){//给Clock对象加锁

                if(os.inter_flag){//线程同步，确保每次时钟中断都进行了调度

                    wait();

                }

                time += 10;

                interrupt(os);

            }

            if(os.end_flag) break;

        }

    }



    public void interrupt(Os os){//时钟中断

        os.inter_flag=true;

        notifyAll();

    }



    public int gettime(){//获取当前时间

        return time;

    }

}



class BUS{//总线类

    private short addbus;//16位地址线

    private short databus;//16位数据线

}





class MMU{//存储管理部件类

    public short add_change(Os os,short vir_add){//将逻辑地址转换为物理地址

        short real_add=0;

        return real_add;

    }

}

class Memory{//内存类

    public int memory[]=new int[64];

    //共32KB,每个物理块大小512B,共64个物理块,-1表示该物理块空闲，非负表示该物理块被相应序号进程占用



    Memory(){//内存初始化

        for(int i=0;i<64;i++)

            memory[i]=-1;

    }



}



class Disk{//硬盘类

    public int disk[][]=new int[32][64];

    //1 个磁道中有64个扇区，1个柱面中有32个磁道,1个扇区为1个物理块，每个物理块大小512B，合计1MB

    //非负表示被相应序号的进程占用，-1表示空闲



    Disk(){

        for(int i=0;i<32;i++)

            for(int j=0;j<64;j++)

                disk[i][j] = -1;

        File directory=new File(".");

        String path=null;

        try{

            path=directory.getCanonicalPath();

        }catch(IOException e){

            e.printStackTrace();

        }

        path+="\\disk";

        File file=new File(path);

        if(!file.exists())

            file.mkdir();

        String sonpath=null;

        for(int i=0;i<32;i++){

            sonpath=path+"\\track"+String.valueOf(i+1)+".txt";

            File sonfile=new File(sonpath);

            if(!sonfile.exists()){

                try {

                    sonfile.createNewFile();

                }catch(IOException e){

                    e.printStackTrace();

                }

            }

            try{

                FileOutputStream out=new FileOutputStream(sonfile);

                PrintStream p=new PrintStream(out);

                for(int j=0;j<64;j++) {

                    p.print("扇区" + j + ":" );

                    if(disk[i][j]==-1)

                        p.println("空闲");

                    else

                        p.println(disk[i][j]+"号进程");

                }

                out.close();

                p.close();

            }catch(FileNotFoundException e){

                e.printStackTrace();

            }catch(IOException e){

                e.printStackTrace();

            }

        }

    }



    public void write(){

        File directory=new File(".");

        String path=null;

        try{

            path=directory.getCanonicalPath();

        }catch(IOException e){

            e.printStackTrace();

        }

        path+="\\disk";

        String sonpath=null;

        for(int i=0;i<32;i++){

            sonpath=path+"\\track"+String.valueOf(i+1)+".txt";

            File sonfile=new File(sonpath);

            try{

                FileOutputStream out=new FileOutputStream(sonfile);

                PrintStream p=new PrintStream(out);

                for(int j=0;j<64;j++) {

                    p.print("扇区" + j + ":" );

                    if(disk[i][j]==-1)

                        p.println("空闲");

                    else

                        p.println(disk[i][j]+"号进程");

                }

                out.close();

                p.close();

            }catch(FileNotFoundException e){

                e.printStackTrace();

            }catch(IOException e){

                e.printStackTrace();

            }

        }
        System.out.println("磁盘占用情况");
        for(int i=0;i<32;i++){

            for(int j=0;j<64;j++) {
                if(disk[i][j]!=-1)
                    System.out.print(disk[i][j] + " ");
                else
                    System.out.print( "空闲 ");
            }

            System.out.println();

        }

    }

}