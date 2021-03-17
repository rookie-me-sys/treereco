package com.nebulord.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OdmContainer {
    private String containerName;
    private String containerId;
    private String userid;
    private String status;
}
