package com.galaxyinternet.rili.model;

import com.galaxyinternet.framework.core.model.PagableEntity;

/**
 * 消息-user 关联
 */
public class ScheduleMessageUser extends PagableEntity {

	private static final long serialVersionUID = 1L;

	private Long mid; // 消息 id

    private Long uid; // 接收人 id

    private String uname; 

    private Byte typeRole; //会议(1:组织人 2:受邀人) 拜访(3:去拜访者 4:被拜访人)

    private byte isUse;  //0:可用    1:禁用

    private Byte isSend; //0:未发送  1+:已发送

    private Byte isRead; //0:未读    1:已读

    private Byte isDel;  //0:未删除  1:已删除

    
    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname == null ? null : uname.trim();
    }

    public Byte getTypeRole() {
        return typeRole;
    }

    public void setTypeRole(Byte typeRole) {
        this.typeRole = typeRole;
    }

    public byte getIsUse() {
        return isUse;
    }

    public void setIsUse(byte isUse) {
        this.isUse = isUse;
    }

    public Byte getIsSend() {
        return isSend;
    }

    public void setIsSend(Byte isSend) {
        this.isSend = isSend;
    }

    public Byte getIsRead() {
        return isRead;
    }

    public void setIsRead(Byte isRead) {
        this.isRead = isRead;
    }

    public Byte getIsDel() {
        return isDel;
    }

    public void setIsDel(Byte isDel) {
        this.isDel = isDel;
    }


}