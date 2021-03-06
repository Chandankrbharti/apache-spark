package apache.spark.poc.kafka.producer;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.log4j.Logger;

public class IntegerPartitioner implements Partitioner {
  
//    AtomicInteger partitionId = new AtomicInteger()
//  
//    private static int getPartitionId() {
//      
//    }
  
    private static Map<String,Integer> IntegerPartitioner;
    
    private Logger logger = Logger.getLogger(IntegerPartitioner.class);

    // This method will gets called at the start, you should use it to do one time startup activity
    public void configure(Map<String, ?> configs) {
        logger.info("Inside IntegerPartitioner.configure " + configs);
        IntegerPartitioner = new HashMap<String, Integer>();
        for(Map.Entry<String,?> entry: configs.entrySet()){
            if(entry.getKey().startsWith("partitions.")){
                String keyName = entry.getKey();
                String value = (String)entry.getValue();
                logger.info( keyName.substring(11));
                int paritionId = Integer.parseInt(keyName.substring(11));
                IntegerPartitioner.put(value,paritionId);
            }
        }
    }

    //This method will get called once for each message
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes,
                         Cluster cluster) {
        List partitions = cluster.availablePartitionsForTopic(topic);
        String valueStr = (String)value;
        String countryName = ((String) value).split(":")[0];
        if(IntegerPartitioner.containsKey(countryName)){
            //If the country is mapped to particular partition return it
            return IntegerPartitioner.get(countryName);
        }else {
            //If no country is mapped to particular partition distribute between remaining partitions
            int noOfPartitions = cluster.topics().size();
            return  value.hashCode()%noOfPartitions + IntegerPartitioner.size() ;
        }
    }

    // This method will get called at the end and gives your partitioner class chance to cleanup
    public void close() {}
}