/**
 * 海到尽头天作岸 山登绝顶我为峰
 *
 * @Author Administrator
 * @CreateTime 2026-05-14 14:27
 * @Description
 **/
package com.weiyj.common.util;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.PhysicalMemory;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SystemMonitor {

    public  static SystemInfo systemInfo = new SystemInfo();

    //获取CPU状态
    public static Map<String,Object> getCpuStatus(){

        Map<String, Object> status = new HashMap<>();
        CentralProcessor processor = systemInfo.getHardware().getProcessor();
        //处理器名
        String cpu_name = processor.getProcessorIdentifier().getName();
        //核心数
        int core_count = processor.getLogicalProcessorCount();
        //使用率
        double cpu_load = processor.getSystemCpuLoad(1000) * 100;

        status.put("cpu_name",cpu_name);
        status.put("description","CPU状态");
        status.put("core_count",core_count);
        status.put("cpu_load",String.format("%.2f",cpu_load));

        return status;
    }
    // 内存
    public static Map getMemoryStatus(){
        Map<String, Object> status = new HashMap<>();
        GlobalMemory memory = systemInfo.getHardware().getMemory();

        //List<PhysicalMemory> physicalMemories = memory.getPhysicalMemory();
        //总内存
        double totalMemory = memory.getTotal() / 1024 / 1024;
        //可用内存
        double availableMemory = memory.getAvailable() / 1024 / 1024;
        //已用内存
        double usedMemory = totalMemory - availableMemory;
        //使用率
        double memoryLoad =((totalMemory-availableMemory)/totalMemory)*100;

        status.put("description","内存状态");
        status.put("total_memory",String.format("%.2f",totalMemory / 1024));
        status.put("used_memory",String.format("%.2f",usedMemory / 1024));
        status.put("available_memory",String.format("%.2f",availableMemory / 1024));
        status.put("memoryLoad",String.format("%.2f",memoryLoad));

        return status;

    }
    // 获取磁盘
    public static Map getFilesStatus(){

        Map<String, Object> status = new HashMap<>();
        FileSystem fileSystem = systemInfo.getOperatingSystem().getFileSystem();
        List<OSFileStore> fileStores = fileSystem.getFileStores();
        double total_space = fileStores.stream().mapToDouble(OSFileStore::getTotalSpace).sum()/1024/1024/1024;
        double usable_space = fileStores.stream().mapToDouble(OSFileStore::getUsableSpace).sum()/1024/1024/1024;
        double used_space = total_space - usable_space;
        status.put("description","文件系统状态");
        status.put("total_space",String.format("%.2f",total_space));
        status.put("usable_space",String.format("%.2f",usable_space));
        status.put("used_space",String.format("%.2f",used_space));
        return status;
    }


    //获取系统状态
    public static Map getStatus(){
        try {
            Map status = new HashMap<>();
            status.put("cpu_status",getCpuStatus());
            status.put("memory_status",getMemoryStatus());
            status.put("files_sys_status",getFilesStatus());
            return status;
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("获取机器状态出错");
        }


    }



}
