package com.remulasce.lametroapp.java_core.analytics;

// Ok, finally - Nighelles

public class IosLog extends Log {
    private String logToDate = "";
    
    public void print_d(String tag, String msg)
    {
        logToDate = logToDate + "Log.d Tag: " + tag + " Msg:" + msg + "\n";
    }
    
    public void print_e(String tag, String msg)
    {
        logToDate = logToDate + "Log.d Tag: " + tag + " Msg:" + msg + "\n";
    }
    public void print_v(String tag, String msg)
    {
        logToDate = logToDate + "Log.d Tag: " + tag + " Msg:" + msg + "\n";
    }
    public void print_i(String tag, String msg)
    {
        logToDate = logToDate + "Log.d Tag: " + tag + " Msg:" + msg + "\n";
    }
    public void print_w(String tag, String msg)
    {
        logToDate = logToDate + "Log.d Tag: " + tag + " Msg:" + msg + "\n";
    }
    
    public String getLogToDate()
    {
        String logToReturn = logToDate;
        logToDate = "";
        return logToReturn;
    }
}