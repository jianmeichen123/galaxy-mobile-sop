package com.galaxyinternet.model.project;

import java.util.Date;

import com.galaxyinternet.framework.core.model.PagableEntity;

public class PersonPool extends PagableEntity{
	private static final long serialVersionUID = 1L;
	
	public final static String ID = "团队成员id";
	public final static String PERSONNAME = "团队成员名称";
	private Long tid;
    private String personName;
    private Integer personSex;
    private Integer personAge;
    private String highestDegree;
    private Integer workTime;
    private String personDuties;
    private Date personBirthday;
    private String personIdcard;
    private String personTelephone;
    private String personEmail;
    private String personCharacter;
    private String personGoodness;
    private String personDisparity;
    private String talkAbility;
    private String teamAbility;
    private String businessStrength;
    private Integer free;
    private String teamRole;
    private String memberRelation;
    private Integer laborDispute;
    private Integer abilityStar;
    private Integer levelStar;
    private String endComment;
    private Long createId;
    
    //添加的 时间字段
    private String personBirthdayStr;
    private String formatAgeStr;
    private String formatWorkTime;
    
  //  2011/11/22  新增两个字段为了app端 新增的团队成员简历
    /*是否为联系人*/
    private Integer isContacts;
    /*备注*/
    private String remark;
    
    //添加年龄 2016/11/23
    private Integer age;
    //为了app端选出最高学历新添加 字段
    private Integer intagree;
    //学历
    private String agree;
    //学校
    private String school;
    
    //2016/12/30新一期上线新增4个字段
    private String filePath;       //文件的地址
    private String fileLength;    //文件的长度
    private String fileName;      //文件的名字
    private String fileSuffix;    //文件的后缀
    
    
    
	public String getFormatWorkTime() {
		return formatWorkTime;
	}
	public void setFormatWorkTime(String formatWorkTime) {
		this.formatWorkTime = formatWorkTime;
	}
	
	public String getFormatAgeStr() {
		return formatAgeStr;
	}
	public void setFormatAgeStr(String formatAgeStr) {
		this.formatAgeStr = formatAgeStr;
	}
	
	
	public String getPersonBirthdayStr() {
		return personBirthdayStr;
	}
	public void setPersonBirthdayStr(String personBirthdayStr) {
		this.personBirthdayStr = personBirthdayStr;
	}
	public Long getTid() {
		return tid;
	}
	public void setTid(Long tid) {
		this.tid = tid;
	}
	public String getPersonName() {
        return personName;
    }
    public void setPersonName(String personName) {
        this.personName = personName == null ? null : personName.trim();
    }

    public Integer getPersonSex() {
        return personSex;
    }

    public void setPersonSex(Integer personSex) {
        this.personSex = personSex;
    }

    public Integer getPersonAge() {
    	if(this.formatAgeStr != null && !"".equals(this.formatAgeStr.trim())){
			this.personAge = Integer.parseInt(this.formatAgeStr.trim());
		}
		return personAge;
	}

	public void setPersonAge(Integer personAge) {
		this.personAge = personAge;
	}

	public String getHighestDegree() {
		return highestDegree;
	}

	public void setHighestDegree(String highestDegree) {
		this.highestDegree = highestDegree;
	}

	public Integer getWorkTime() {
		if(this.formatWorkTime != null && !"".equals(this.formatWorkTime.trim())){
			this.workTime = Integer.parseInt(this.formatWorkTime.trim());
		}
		return workTime;
	}

	public void setWorkTime(Integer workTime) {
		this.workTime = workTime;
	}

	public String getPersonDuties() {
		return personDuties;
	}

	public void setPersonDuties(String personDuties) {
		this.personDuties = personDuties;
	}

	public Date getPersonBirthday() {
        return personBirthday;
    }

    public void setPersonBirthday(Date personBirthday) {
        this.personBirthday = personBirthday;
    }

    public String getPersonIdcard() {
        return personIdcard;
    }

    public void setPersonIdcard(String personIdcard) {
        this.personIdcard = personIdcard == null ? null : personIdcard.trim();
    }

    public String getPersonTelephone() {
        return personTelephone;
    }

    public void setPersonTelephone(String personTelephone) {
        this.personTelephone = personTelephone == null ? null : personTelephone.trim();
    }

    public String getPersonEmail() {
        return personEmail;
    }

    public void setPersonEmail(String personEmail) {
        this.personEmail = personEmail == null ? null : personEmail.trim();
    }

    public String getPersonCharacter() {
        return personCharacter;
    }

    public void setPersonCharacter(String personCharacter) {
        this.personCharacter = personCharacter == null ? null : personCharacter.trim();
    }

    public String getPersonGoodness() {
        return personGoodness;
    }

    public void setPersonGoodness(String personGoodness) {
        this.personGoodness = personGoodness == null ? null : personGoodness.trim();
    }

    public String getPersonDisparity() {
        return personDisparity;
    }

    public void setPersonDisparity(String personDisparity) {
        this.personDisparity = personDisparity == null ? null : personDisparity.trim();
    }

    public String getBusinessStrength() {
        return businessStrength;
    }

    public void setBusinessStrength(String businessStrength) {
        this.businessStrength = businessStrength == null ? null : businessStrength.trim();
    }

    public Integer getFree() {
        return free;
    }

    public void setFree(Integer free) {
        this.free = free;
    }

    public String getTeamRole() {
        return teamRole;
    }

    public void setTeamRole(String teamRole) {
        this.teamRole = teamRole == null ? null : teamRole.trim();
    }

    public String getMemberRelation() {
        return memberRelation;
    }

    public void setMemberRelation(String memberRelation) {
        this.memberRelation = memberRelation == null ? null : memberRelation.trim();
    }

    public Integer getLaborDispute() {
        return laborDispute;
    }

    public void setLaborDispute(Integer laborDispute) {
        this.laborDispute = laborDispute;
    }

    public Integer getAbilityStar() {
        return abilityStar;
    }

    public void setAbilityStar(Integer abilityStar) {
        this.abilityStar = abilityStar;
    }

    public Integer getLevelStar() {
        return levelStar;
    }

    public void setLevelStar(Integer levelStar) {
        this.levelStar = levelStar;
    }

    public String getEndComment() {
        return endComment;
    }

    public void setEndComment(String endComment) {
        this.endComment = endComment == null ? null : endComment.trim();
    }

    public Long getCreateId() {
        return createId;
    }

    public void setCreateId(Long createId) {
        this.createId = createId;
    }

	public String getTalkAbility() {
		return talkAbility;
	}

	public void setTalkAbility(String talkAbility) {
		this.talkAbility = talkAbility;
	}

	public String getTeamAbility() {
		return teamAbility;
	}

	public void setTeamAbility(String teamAbility) {
		this.teamAbility = teamAbility;
	}
	public Integer getIsContacts() {
		return isContacts;
	}
	public void setIsContacts(Integer isContacts) {
		this.isContacts = isContacts;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Integer getIntagree() {
		return intagree;
	}
	public void setIntagree(Integer intagree) {
		this.intagree = intagree;
	}
	public String getAgree() {
		return agree;
	}
	public void setAgree(String agree) {
		this.agree = agree;
	}
	public String getSchool() {
		return school;
	}
	public void setSchool(String school) {
		this.school = school;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}

	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getFileLength() {
		return fileLength;
	}
	public void setFileLength(String fileLength) {
		this.fileLength = fileLength;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileSuffix() {
		return fileSuffix;
	}
	public void setFileSuffix(String fileSuffix) {
		this.fileSuffix = fileSuffix;
	}
    
	
    
}