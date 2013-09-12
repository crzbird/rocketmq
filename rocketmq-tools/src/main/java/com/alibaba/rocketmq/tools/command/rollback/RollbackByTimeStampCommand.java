package com.alibaba.rocketmq.tools.command.rollback;

import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import com.alibaba.rocketmq.common.MixAll;
import com.alibaba.rocketmq.common.UtilALl;
import com.alibaba.rocketmq.common.admin.RollbackStats;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.alibaba.rocketmq.tools.command.SubCommand;


/**
 * 根据时间回溯消费进度
 * 
 * @author: manhong.yqd<jodie.yqd@gmail.com>
 * @since: 13-9-12
 */
public class RollbackByTimeStampCommand implements SubCommand {
    @Override
    public String commandName() {
        return "rollbackByTimeStamp";
    }


    @Override
    public String commandDesc() {
        return "rollback consumer offset by timestamp.";
    }


    @Override
    public Options buildCommandlineOptions(Options options) {
        Option opt = new Option("g", "group", true, "set the consumer group");
        opt.setRequired(true);
        options.addOption(opt);

        opt = new Option("t", "topic", true, "set the topic");
        opt.setRequired(true);
        options.addOption(opt);

        opt =
                new Option("s", "timestamp", true,
                    "set the timestamp[currentTimeMillis|yyyy-MM-dd HH:mm:ss:SSS]");
        opt.setRequired(true);
        options.addOption(opt);

        opt = new Option("f", "force", true, "set the force rollback by timestamp switch[true|false]");
        opt.setRequired(true);
        options.addOption(opt);
        return options;
    }


    @Override
    public void execute(CommandLine commandLine, Options options) {
        DefaultMQAdminExt defaultMQAdminExt = new DefaultMQAdminExt();
        defaultMQAdminExt.setInstanceName(Long.toString(System.currentTimeMillis()));
        try {
            String consumerGroup = commandLine.getOptionValue("g").trim();
            String topic = commandLine.getOptionValue("t").trim();
	        long timestamp = Long.valueOf(commandLine.getOptionValue("s").trim());
	        boolean force = Boolean.valueOf(commandLine.getOptionValue("f").trim());
            defaultMQAdminExt.start();
            List<RollbackStats> rollbackStatsList =
                    defaultMQAdminExt.rollbackConsumerOffset(consumerGroup, topic, timestamp, force);
            System.out
                .printf(
                    "rollback consumer offset by specified consumerGroup[%s], topic[%s], timestamp[%s]\n",
                    consumerGroup, topic, timestamp);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            defaultMQAdminExt.shutdown();
        }
    }


    public static void main(String[] args) {
        System.setProperty(MixAll.NAMESRV_ADDR_PROPERTY, "10.232.26.122:9876");
        RollbackByTimeStampCommand cmd = new RollbackByTimeStampCommand();
        Options options = MixAll.buildCommandlineOptions(new Options());
        String[] subargs =
                new String[] { "-g qatest_consumer", "-t qatest_TopicTest", "-s 2013-09-12 17:15:03:000",
                              "-f true" };
        final CommandLine commandLine =
                MixAll.parseCmdLine("mqadmin " + cmd.commandName(), subargs,
                    cmd.buildCommandlineOptions(options), new PosixParser());
        cmd.execute(commandLine, options);
    }
}
