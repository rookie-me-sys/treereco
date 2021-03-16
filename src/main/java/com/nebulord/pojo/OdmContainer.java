package com.nebulord.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OdmContainer {
    String containerName;
    String containerId;
    String userid;
}
