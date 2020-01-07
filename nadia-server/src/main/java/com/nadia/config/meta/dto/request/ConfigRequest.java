package com.nadia.config.meta.dto.request;

import com.nadia.config.validation.Addition;
import com.nadia.config.validation.Updation;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author: Wally.Wang
 * @date: 2019/12/04
 * @description:
 */
@Data
public class ConfigRequest extends BaseRequest {

    private String application;
    private String group;
    @NotBlank(message = "name could not be blank", groups = Addition.class)
    private String name;
    @NotBlank(message = "key could not be blank", groups = Addition.class)
    private String key;
    @NotNull(message = "applicationId could not be null", groups = Addition.class)
    private Long applicationId;
    @NotNull(message = "groupId could not be null", groups = Addition.class)
    private Long groupId;
    @NotNull(message = "id could not be null", groups = Updation.class)
    private Long id;
    @NotBlank(message = "value could not be blank")
    private String value;
    @NotBlank(message = "description could not be blank")
    private String description;
    private List<Long> roleIds;

}
