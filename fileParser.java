import java.io.*;
import java.util.*;

public class fileParser
{
    private static String getConcatInfo(String line)
    {
        if(line.contains("OUTPUTMethodNCallsChangedOldToNew"))
        {
            return null;
            /*
             String getOutput = line.split("OUTPUTMethodNCallsChangedOldToNew")[1];
             String[] oSSpace = getOutput.split(" ");
             String oldNCalls = oSSpace[0];
             String newNCalls = oSSpace[1];
             String methodCall = oSSpace[2];
             
             return String.format("%s%s%s", methodCall, oldNCalls, newNCalls);
             */
        }
        else if(line.contains("OUTPUTMethodCallAdded"))
        {
            String methodCall = line.split("OUTPUTMethodCallAdded")[1];
            return String.format("%s--added", methodCall);
        }
        else if(line.contains("OUTPUTMethodCallRemoved"))
        {
            String methodCall = line.split("OUTPUTMethodCallRemoved")[1];
            return String.format("%s--removed", methodCall);
        }
        return null;
    }
    
    public static void main (String [] args) throws IOException
    {
        String inputFile = args[0];

        File inFile = new File (inputFile);
        
        Set<String> call_Descr_Set = new HashSet<String>();
        
        Scanner sc = new Scanner(inFile);
        // Keep a bunch of files so that we can separate them by the method (easier to feed into the KMeans script)
        Map<String, PrintWriter> methodFiles = new HashMap<String, PrintWriter>();
        
        while(sc.hasNextLine())
        {
            String line = sc.nextLine();
            String[] splitBracket = line.split("\\[");
            String[] splitSpace = splitBracket[1].split(" ");
            String[] splitCloseBracket = (splitSpace[1]).split("\\]");
            
            String changeID = splitSpace[0];
            String changedMethod = splitCloseBracket[0];
            String indexFile = String.format("output--%s--%s", changedMethod, changeID);
            
            String concatInfo = getConcatInfo(line);
            if(concatInfo != null)
            {
                String call_indexer = String.format("%s%s", changeID, concatInfo);
                if(!methodFiles.containsKey(indexFile))
                {
                    methodFiles.put(indexFile, new PrintWriter(indexFile, "UTF-8"));
                    call_Descr_Set.add(call_indexer);
                    methodFiles.get(indexFile).println(concatInfo);
                }
                else if(!call_Descr_Set.contains(call_indexer))
                {
                    call_Descr_Set.add(call_indexer);
                    methodFiles.get(indexFile).println(concatInfo);
                }
            }
        }
        sc.close();
        Iterator fileMapCleaner = methodFiles.entrySet().iterator();
        while(fileMapCleaner.hasNext())
        {
            Map.Entry pair = (Map.Entry)fileMapCleaner.next();
            ((PrintWriter) pair.getValue()).close();
            fileMapCleaner.remove();
        }
        
    }
}
