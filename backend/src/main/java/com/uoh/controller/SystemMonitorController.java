package com.uoh.controller;

import com.uoh.common.BaseResponse;
import com.uoh.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/monitor")
public class SystemMonitorController {
    @GetMapping("/status")
    public BaseResponse<Map<String, Object>> getSystemStatus() {
        // 1. 初始化 SystemInfo
        oshi.SystemInfo si = new oshi.SystemInfo();

        // 2. 获取硬件层 (包含 CPU, 内存)
        oshi.hardware.HardwareAbstractionLayer hal = si.getHardware();

        // 3. 获取操作系统层 (包含 运行时间, 进程)
        oshi.software.os.OperatingSystem os = si.getOperatingSystem();

        // 4. 获取具体的硬件对象
        oshi.hardware.CentralProcessor processor = hal.getProcessor();
        oshi.hardware.GlobalMemory memory = hal.getMemory();

        // --- CPU 使用率计算 ---
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        try {
            Thread.sleep(500); // 采样 0.5 秒
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        double cpuUsage = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100;

        // --- 内存计算 ---
        long totalByte = memory.getTotal();
        long availableByte = memory.getAvailable();
        double totalMem = totalByte / 1024.0 / 1024.0 / 1024.0;
        double usedMem = (totalByte - availableByte) / 1024.0 / 1024.0 / 1024.0;

        // --- 组装数据 ---
        Map<String, Object> data = new HashMap<>();
        data.put("cpuUsage", Math.round(cpuUsage * 10) / 10.0);
        data.put("memUsage", Math.round((usedMem / totalMem * 100) * 10) / 10.0);
        data.put("memTotal", Math.round(totalMem * 10) / 10.0);
        data.put("memUsed", Math.round(usedMem * 10) / 10.0);

        // 系统属性
        data.put("os", System.getProperty("os.name"));
        data.put("javaVersion", System.getProperty("java.version"));
        // 运行时间：从操作系统对象获取
        data.put("upTime", formatUptime(os.getSystemUptime()));

        return ResultUtils.success(data);
    }

    // 格式化运行时间的方法（放在同一个类里）
    private String formatUptime(long seconds) {
        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long mins = (seconds % 3600) / 60;
        return String.format("%d天%d小时%d分", days, hours, mins);
    }
}