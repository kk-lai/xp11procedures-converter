package com.fg;

public class App 
{
    public static void main( String[] args )
    {
        if (args.length<2) {
            System.err.println("xpextractor <func> <arg>");
            return;
        }
        if (args[0].equals("-fix")) {
            FixDB db = new FixDB();
            db.dat2db(args[1]);   
        }
        if (args[0].equals("-nav")) {
            NavDB db = new NavDB();
            db.dat2db(args[1]);   
        }
        if (args[0].equals("-airport")) {
            AirportDB db = new AirportDB();
            db.dat2db(args[1]);   
        }
        if (args[0].equals("-proc")) {
            ProcedureDB proc = new ProcedureDB();
            proc.extractProcedure(args[1], args[2], args[3]);
        }
    }
}
