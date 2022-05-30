package cs.com.imageexpressioncalculation;

//
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by KimChi on 12/27/2015.
 */
public class ExpressionValueCalc {

    public ExpressionValueCalc(){

    }

    public static int priority(String s) //xét độ ưu tiên các toán tử
    {
        if (s.equals("+")  |s.equals("-") )
        {
            return 1;
        }
        else if (s.equals("*") | s.equals("/"))
        {
            return 2;
        }
        else
        {
            return 0;
        }
    }

    public static List<String> InfixToPostfix(String Infix)
    {
        int j = 0;
        List<String> Postfix = new ArrayList<>();
        Stack Operator = new Stack();
        for(int i = 0 ; i < Infix.length(); i++){

            if((String.valueOf(Infix.charAt(i)).equals("(")))
            {
                Operator.push(Infix.charAt(i)+"");
            }
            else if((String.valueOf(Infix.charAt(i)).equals(")")))
            {
                String p = (String)Operator.pop();
                if (!p.equals("("))
                {
                    while (!p.equals("("))
                    {
                        Postfix.add(j, p);
                        j++;
                        if(Operator.size() > 0) p = (String)Operator.pop();
                    }
                    if(Operator.empty() == false)
                        if(Operator.peek().equals("("))
                        {
                            Operator.pop();
                        }
                }
            }
            else if (Infix.charAt(i) == '+' | Infix.charAt(i) == '-'
                    | Infix.charAt(i) == '*' | Infix.charAt(i) == '/')
            {
                String p = "";
                if (Operator.size() > 0) p = (String)Operator.peek();
                while (Operator.size() > 0 && priority(p) >= priority(Infix.charAt(i)+""))
                {
                    p = (String)(Operator.pop() + "");
                    Postfix.add(j, p);
                    j++;
                    if(Operator.size() > 0) p = (String)Operator.peek();
                }
                Operator.push(Infix.charAt(i)+"");
            }
            else
            {
                String temp="";
                while(i < Infix.length() && Infix.charAt(i) != '(' && Infix.charAt(i)!= ')' && Infix.charAt(i) != '+' && Infix.charAt(i) != '-'
                        && Infix.charAt(i) != '*' && Infix.charAt(i) != '/')
                {
                    if(Infix.charAt(i) != ' ') temp +=  Infix.charAt(i);
                    i++;
                }
                i--;
                if(!temp.equals("") )
                {
                    Postfix.add(j, temp);
                    j++;
                }
            }
        }
        while(Operator.size() > 0)
        {
            String p = (String)Operator.pop();
            Postfix.add(j, p);
            j++;
        }
        return Postfix;
    }

    public static double PostfixCalculate( List<String> Postfix)//tính toán dạng postfix
    {
        Stack KQ = new Stack();
        int cancalc = 0;
        for(int i = 0; i < Postfix.size(); i++)
        {
            if (((String)Postfix.get(i)).equals("+"))
            {
                double a = (double)KQ.pop();
                double b = (double)KQ.pop();
                double c = a+b;

                KQ.push(c);
            }
            else if (((String)Postfix.get(i)).equals("-"))
            {
                double a = (double)KQ.pop();
                double b = (double)KQ.pop();
                double c = b - a;

                KQ.push(c);
            }
            else if (((String)Postfix.get(i)).equals("*"))
            {
                double a = (double)KQ.pop();
                double b = (double)KQ.pop();
                double c = a*b;

                KQ.push(c);
            }
            else if (((String)Postfix.get(i)).equals("/"))
            {
                double a = (double)KQ.pop();
                double b = (double)KQ.pop();
                double c = b/a;

                KQ.push(c);
            }
            else
            {
                double c;
                c = Double.parseDouble((String)Postfix.get(i));
                KQ.push(c);
            }
        }
        return (double)KQ.pop();
    }


}
