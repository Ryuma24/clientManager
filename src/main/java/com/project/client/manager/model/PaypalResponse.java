package com.project.client.manager.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

@JsonSerialize
@Data
public class PaypalResponse {
    private String webhook_id;
    private String url;
    private String event_type;
    private String resource_version;
}
