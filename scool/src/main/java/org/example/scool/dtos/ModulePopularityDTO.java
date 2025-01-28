package org.example.scool.dtos;

import lombok.Data;

@Data
public class ModulePopularityDTO {
    private String moduleCode;
    private String moduleName;
    private long enrollmentCount; // Use long instead of int

    public ModulePopularityDTO(String moduleCode, String moduleName, long enrollmentCount) {
        this.moduleCode = moduleCode;
        this.moduleName = moduleName;
        this.enrollmentCount = enrollmentCount;
    }
}