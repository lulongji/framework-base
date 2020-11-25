package com.lulj.base;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LVersion {

    public LVersion(String version) {
        log.info("Startup complete, current version ：" + version);
    }
}
