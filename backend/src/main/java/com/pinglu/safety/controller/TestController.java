package com.pinglu.safety.controller;

import com.pinglu.safety.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "System Test")
@RestController
@RequestMapping("/api")
public class TestController {

    @Operation(summary = "Health check test endpoint")
    @GetMapping("/test")
    public Result<String> test() {
        return Result.success("PingLu Canal AI Safety System is running");
    }
}
