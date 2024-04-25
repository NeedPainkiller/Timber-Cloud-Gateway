package xyz.needpankiller.timber.gateway.lib.http;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuditLogMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 936891743559846237L;

    public AuditLogMessage() {
    }

    private Long id;
    private Long tenantPk;
    private boolean visibleYn;
    private Integer httpStatus;
    private HttpMethod httpMethod;
    private String agentOs;
    private String agentOsVersion;
    private String agentBrowser;
    private String agentBrowserVersion;
    private String agentDevice;
    private String requestIp;
    private String requestUri;
    private String requestContentType;
    private String requestPayLoad;
    private String responseContentType;
    private String responsePayLoad;
    private Timestamp createdDate;
    private Map<String, Serializable> errorData;

    private String token;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isVisibleYn() {
        return visibleYn;
    }

    public void setVisibleYn(boolean visibleYn) {
        this.visibleYn = visibleYn;
    }

    public Long getTenantPk() {
        return tenantPk;
    }

    public void setTenantPk(Long tenantPk) {
        this.tenantPk = tenantPk;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(Integer httpStatus) {
        this.httpStatus = httpStatus;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getAgentOs() {
        return agentOs;
    }

    public void setAgentOs(String agentOs) {
        this.agentOs = agentOs;
    }

    public String getAgentOsVersion() {
        return agentOsVersion;
    }

    public void setAgentOsVersion(String agentOsVersion) {
        this.agentOsVersion = agentOsVersion;
    }

    public String getAgentBrowser() {
        return agentBrowser;
    }

    public void setAgentBrowser(String agentBrowser) {
        this.agentBrowser = agentBrowser;
    }

    public String getAgentBrowserVersion() {
        return agentBrowserVersion;
    }

    public void setAgentBrowserVersion(String agentBrowserVersion) {
        this.agentBrowserVersion = agentBrowserVersion;
    }

    public String getAgentDevice() {
        return agentDevice;
    }

    public void setAgentDevice(String agentDevice) {
        this.agentDevice = agentDevice;
    }

    public String getRequestIp() {
        return requestIp;
    }

    public void setRequestIp(String requestIp) {
        this.requestIp = requestIp;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public String getRequestContentType() {
        return requestContentType;
    }

    public void setRequestContentType(String requestContentType) {
        this.requestContentType = requestContentType;
    }

    public String getRequestPayLoad() {
        return requestPayLoad;
    }

    public void setRequestPayLoad(String requestPayLoad) {
        this.requestPayLoad = requestPayLoad;
    }

    public String getResponseContentType() {
        return responseContentType;
    }

    public void setResponseContentType(String responseContentType) {
        this.responseContentType = responseContentType;
    }

    public String getResponsePayLoad() {
        return responsePayLoad;
    }

    public void setResponsePayLoad(String responsePayLoad) {
        this.responsePayLoad = responsePayLoad;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public Map<String, Serializable> getErrorData() {
        return errorData;
    }

    public void setErrorData(Map<String, Serializable> errorData) {
        this.errorData = errorData;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}