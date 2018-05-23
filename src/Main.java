import java.util.Scanner;

public class Main {
    public static int analy_num = 0;  //文法个数
    public static char[] left = new char[10];  //文法左部
    public static String[] right = new String[10];  //文法右部
    public static int NoSybol_num = 0;  //非终结符个数
    public static int Sybol_num = 0;  //终结符个数
    public static char[] NoSybol = new char[10];  //非终结符
    public static char[] Sybol = new char[10];  //终结符
    //    public static char[] Sybol_1=new char[10];  //去除$符的终结符
    public static String[] First = new String[10];  //first集
    public static String[] Follow = new String[10];  //Follow集
    public static int[][] table = new int[10][10];  //预测分析表
    public static char[] Stack = new char[20];  //分析栈
    public static int top = -1;  //栈顶
    public static String input_str;  //输入的符号串

    public static void main(String[] args) {
        //输入文法
        input_str();
        //first
        for (int i = 0; i < NoSybol_num; i++) {
            get_first(NoSybol[i]);  //求非终结符的first集
        }
        System.out.println("\nFIRST集为：");
        for (int i = 0; i < NoSybol_num; i++) {
            First[i] = removerepeatedchar(First[i]);
            System.out.println(First[i]);  //求非终结符的first集
        }
        //follow
        Follow[0] = "#";
        for (int i = 0; i < NoSybol_num; i++) {
            get_follow(NoSybol[i]);  //求非终结符的follow集
        }
        System.out.println("\nFOLLOW集为：");
        for (int i = 0; i < NoSybol_num; i++) {
            Follow[i] = removerepeatedchar(Follow[i]);
            System.out.println(Follow[i]);  //求非终结符的follow集
        }
        //table
        Sybol[Sybol_num] = '#';
        Sybol_num++;
        init_table();  //初始化预测表
        get_table();  //生成预测表
        for (int i = 0; i < Sybol_num; i++) {
            System.out.print("        " + Sybol[i]);
        }
        System.out.println();
        for (int i = 0; i < NoSybol_num; i++) {
            System.out.print(NoSybol[i]);
            for (int j = 0; j < Sybol_num; j++) {
                System.out.print("    ");
                if (table[i][j] == -1)
                    System.out.print("     ");
                else
                    System.out.print(left[table[i][j]] + "-->" + right[table[i][j]]);
            }
            System.out.println();
        }
        //分析栈
        analy_stack();
    }

    //判断是非终结符
    public static boolean isNotSymbol(char ch) {
        if (ch >= 'A' && ch <= 'Z')
            return true;
        return false;
    }

    //得到非终结符下标
    public static int get_index(char ch) {
        for (int i = 0; i < NoSybol_num; i++) {
            if (ch == NoSybol[i])
                return i;
        }
        return 100; //即没有找到
    }

    //得到终结符下标
    public static int get_Nindex(char ch) {
        for (int i = 0; i < Sybol_num; i++) {
            if (ch == Sybol[i])
                return i;
        }
        return 100; //即没有找到
    }

    //去除字符串中重复值
    public static String removerepeatedchar(String str) {
        String sb = "";
        int flag;
        if (str == null)
            return str;
        for (int i = 0; i < str.length(); i++) {
            flag = 0;
            char ch = str.charAt(i);
            if (sb == "")
                sb += ch;
            else {
                for (int j = 0; j < sb.length(); j++) {
                    if (ch == sb.charAt(j)) {
                        flag = 1;
                        break;
                    }
                }
                if (flag == 0)
                    sb += ch;
            }
        }
        return sb;
    }

    //判断字符在字符串中
    public static boolean inString(char s, String str) {
        for (int i = 0; i < str.length(); i++) {
            if (s == str.charAt(i))
                return true;
        }
        return false;
    }

    //输入文法
    public static void input_str() {
        String[] s = new String[100];  //输入文法
        System.out.print("请输入文法个数：");
        Scanner st = new Scanner(System.in);
        analy_num = st.nextInt();  //文法数量
        for (int i = 0; i < analy_num; i++) {
            left[i] = st.next().charAt(0);
            right[i] = st.next();
        }
        System.out.println("输入产生式为：");
        for (int i = 0; i < analy_num; i++) {
            System.out.println(left[i] + "-->" + right[i]);
        }
        for (int i = 0; i < analy_num; i++) {
            //把文法左部加入非终结符数组
            int flag = 0; //标志位
            if (NoSybol_num == 0) {
                NoSybol[NoSybol_num] = left[i];
                NoSybol_num++;
            } else {
                for (int j = 0; j < NoSybol_num; j++) {
                    if (left[i] == NoSybol[j]) {
                        flag = 1;
                    }
                    if (flag == 1)
                        break;
                }
                if (flag == 0) {
                    NoSybol[NoSybol_num] = left[i];
                    NoSybol_num++;
                }
            }
            //把文法右部加入非终结符数组
            for (int j = 0; j < right[i].length(); j++) {
                char ch = right[i].charAt(j);
                //将文法右部的非终结符加入
                if (isNotSymbol(ch)) {
                    int flag1 = 0;
                    for (int k = 0; k < NoSybol_num; k++) {
                        if (ch == NoSybol[k]) {
                            flag1 = 1;
                        }
                        if (flag1 == 1)
                            break;
                    }
                    if (flag1 == 0) {
                        NoSybol[NoSybol_num] = ch;
                        NoSybol_num++;
                    }
                }
                //将文法右部终结符加入
                else {
                    int flag2 = 0;
                    if (Sybol_num == 0) {
                        Sybol[Sybol_num] = ch;
                        Sybol_num++;
                    } else {
                        for (int m = 0; m < Sybol_num; m++) {
                            if (ch == Sybol[m]) {
                                flag2 = 1;
                            }
                            if (flag2 == 1)
                                break;
                        }
                        if (flag2 == 0) {
                            Sybol[Sybol_num] = ch;
                            Sybol_num++;
                        }
                    }
                }
            }
        }
        //输出非终结符和终结符
        System.out.println("文法中非终结符为：");
        for (int i = 0; i < NoSybol_num; i++)
            System.out.print(NoSybol[i]);
        System.out.println("\n文法中终结符为：");
        for (int i = 0; i < Sybol_num; i++)
            System.out.print(Sybol[i]);
    }

    //找到first集
    public static void get_first(char target) {
        int flag = 0;
        int sum = 0;
        for (int i = 0; i < analy_num; i++) {
            if (left[i] == target) {
                if (!isNotSymbol(right[i].charAt(0)))//如果是终结符，直接加入first集合
                {
                    if (First[get_index(target)] == null)
                        First[get_index(target)] = String.valueOf(right[i].charAt(0));
                    else
                        First[get_index(target)] += right[i].charAt(0);
                } else {
                    for (int j = 0; j < right[i].length(); j++) {
                        char ch = right[i].charAt(j);
                        if (!isNotSymbol(ch))//如果是终结符，直接加入first集合
                        {
                            if (First[get_index(target)] == null)
                                First[get_index(target)] = String.valueOf(ch);
                            else
                                First[get_index(target)] += ch;
                            break;
                        }
                        get_first(ch);//递归
                        for (int k = 0; k < First[get_index(ch)].length(); k++) {
                            char s = First[get_index(ch)].charAt(k);
                            if (s == '$')
                                flag = 1;
                            else {
                                if (First[get_index(target)] == null)
                                    First[get_index(target)] = String.valueOf(s);
                                else
                                    First[get_index(target)] += s;
                            }

                        }
                        if (flag == 0)
                            break;
                        else {
                            sum += flag;
                            flag = 0;
                        }
                    }
                    if (sum == right[i].length()) {
                        First[get_index(target)] += '$';
                    }
                }
            }
        }
    }

    //找到Follow集
    public static void get_follow(char target) {
        for (int i = 0; i < analy_num; i++) {
            int index = -1; //右部非终结符下标
            for (int j = 0; j < right[i].length(); j++) {
                if (right[i].charAt(j) == target) {
                    index = j;
                    break;
                }
            }
            //如果非终结符不在右部末尾
            while(index >= 0 && index < right[i].length() - 1) {
                char nextch = right[i].charAt(index + 1);
                if (!isNotSymbol(nextch)) {
                    if (Follow[get_index(target)] == null) {
                        Follow[get_index(target)] = String.valueOf(nextch);
                    } else {
                        Follow[get_index(target)] += nextch;
                    }
                    break;
                } else {
                    int flag = 0;  //标志非终结符的First集是否有$
                    for (int k = 0; k < First[get_index(nextch)].length(); k++) {
                        char ch1 = First[get_index(nextch)].charAt(k);
                        if (ch1 == '$') {
                            flag = 1;
                        } else {
                            if (Follow[get_index(target)] == null) {
                                Follow[get_index(target)] = String.valueOf(ch1);
                            } else {
                                Follow[get_index(target)] += ch1;
                            }
                        }
                    }
                    if(flag==0)
                    {
                        break;
                    }
                    if (flag == 1) {
                        index++;
                    }
                }
            }
            if (index == right[i].length() - 1 && left[i] != target) {
                get_follow(left[i]);
                for (int m = 0; m < Follow[get_index(left[i])].length(); m++) {
                    char ch2 = Follow[get_index(left[i])].charAt(m);
                    if (Follow[get_index(target)] == null) {
                        Follow[get_index(target)] = String.valueOf(ch2);
                    } else {
                        Follow[get_index(target)] += ch2;
                    }
                }
            }
        }
    }

    //初始化预测表
    public static void init_table() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                table[i][j] = -1;
            }
        }
    }

    //构造预测分析表
    public static void get_table() {
        for (int i = 0; i < analy_num; i++) {
            char ch = right[i].charAt(0);
            int lindex = get_index(left[i]);
            if (!isNotSymbol(ch)) {
                if (ch != '$') {
                    table[lindex][get_Nindex(ch)] = i;
                } else {
                    for (int j = 0; j < Follow[lindex].length(); j++) {
                        char t = Follow[lindex].charAt(j);
                        table[lindex][get_Nindex(t)] = i;
                    }
                }
            } else {
                int rindex = get_index(ch);
                for (int j = 0; j < First[rindex].length(); j++) {
                    char t = First[rindex].charAt(j);
                    table[lindex][get_Nindex(t)] = i;
                }
                if (inString('$', First[rindex])) {
                    for (int j = 0; j < Follow[lindex].length(); j++) {
                        char t = Follow[lindex].charAt(j);
                        table[lindex][get_Nindex(t)] = i;
                    }
                }
            }
        }
    }

    //入栈
    public static void push(char ch) {
        Stack[++top] = ch;
    }

    //出栈
    public static char pop() {
        char ch = Stack[top];
        top--;
        return ch;
    }

    //取栈顶元素
    public static char get_top() {
        return Stack[top];
    }

    //分析栈
    public static void analy_stack() {
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入符号串:");
        input_str = sc.next();
        push('#');
        push(NoSybol[0]);
        while (top > -1) {
            String str = "";
            for (int i = 0; i < top + 1; i++) {
                str += Stack[i];
            }
            str += "                " + input_str + "                ";
            System.out.print(str);
            char ch1 = get_top();
            char ch2 = input_str.charAt(0);
            if (ch1 == ch2 && ch1 == '#') {
                System.out.println("Accepted!");
                System.out.println("是LL1文法");
                break;
            }
            if (ch1 == ch2) {
                pop();
                input_str = input_str.substring(1, input_str.length());
                System.out.println(ch1 + "匹配");
                continue;
            } else if (table[get_index(ch1)][get_Nindex(ch2)] != -1) {
                int num = table[get_index(ch1)][get_Nindex(ch2)];
                String rStr = right[num];
                pop();
                //  System.out.print(rStr);
                if (!rStr.equals("$")) {
                    for (int i = rStr.length() - 1; i >= 0; i--) {
                        push(rStr.charAt(i));
                    }
                }
                System.out.println(left[num] + "-->" + right[num] + "归约");
                continue;
            } else {
                System.out.println("error");
                continue;
            }
        }
    }
}
