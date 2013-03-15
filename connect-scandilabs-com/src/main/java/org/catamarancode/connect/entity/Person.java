package org.catamarancode.connect.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.catamarancode.connect.entity.type.ContactType;
import org.catamarancode.connect.entity.type.Gender;
import org.catamarancode.connect.entity.type.Priority;
import org.catamarancode.connect.entity.type.Sensitivity;
import org.catamarancode.entity.support.EntityFinder;
import org.catamarancode.entity.support.PersistableBase;
import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * Export: select * from Person INTO OUTFILE '/tmp/person.txt' fields enclosed by '"';
 * Import: load data infile '/tmp/person.txt' into table Person fields enclosed by '"';
 * @author mkvalsvik
 *
 */
@Entity
public class Person extends PersistableBase {

	/**
	 * TODO: Use reflection and the @Entity annotation to populate this during
	 * spring startup. That way, all the Person class needs to do is to declare
	 * this field.
	 */
	public static EntityFinder objects;

	private static Logger logger = LoggerFactory.getLogger(Person.class);

	private String firstName;
	private String middleName;
	private String lastName;
	private String jobTitle;
	private String nickname;
	private String shortName;
	private String maidenName;
	private String spouse;
	private String kidNames;
	private Gender gender;
	private Date birthday;
	private String occupation;
	private String hobby;
	private Sensitivity sensitivity;
	private Priority priority;
	private String subject;
	private String tags;
	private String email1;
	private String email2;
	private String email3;
	private String phone1;
	private String phone2;
	private String phone3;
	private String phone4;
	private String fax1;
	private String fax2;
	private String website;
	private String fax3;
	private ContactType type;
	private List<Note> notes = new ArrayList<Note>();
	private String company;
	private Date nextCallDate;
	private boolean deleted;
	private String comments;
	private String linkedInProfile;
	private String twitterHandle;
	private User user;
	
	@ManyToOne(cascade = { CascadeType.ALL }, optional=false)
	@JoinColumn(name="user_id", nullable=false)
    public User getUser() {
        return user;
    }
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public Date getBirthday() {
		return birthday;
	}
	@Size(min = 2)
	public String getCompany() {
		return company;
	}

	@Transient
	public String getDisplayName() {

		if (!StringUtils.hasText(firstName) && !StringUtils.hasText(lastName)
				&& email1 != null) {
			return this.getEmail1LocalPart();
		}

		return this.getName();
	}

	@Email
	public String getEmail1() {
		return email1;
	}

	/**
	 * TODO: Refactor out into a util
	 * 
	 * @return
	 */
	@Transient
	public String getEmail1LocalPart() {
		if (!StringUtils.hasText(this.email1)) {
			return null;
		}
		int atPos = this.email1.indexOf("@");
		if (atPos > 0) {
			return this.email1.substring(0, atPos);
		}
		return null;
	}

	@Email
	public String getEmail2() {
		return email2;
	}

	@Email
	public String getEmail3() {
		return email3;
	}

	/**
	 * @see http://stackoverflow.com/questions/123559/a-comprehensive-regex-for-phone-number-validation
	 * @return
	 */
	@Pattern(regexp = "^(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$")
	public String getFax1() {
		return fax1;
	}

	@Pattern(regexp = "^(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$")
	public String getFax2() {
		return fax2;
	}

	@Pattern(regexp = "^(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$")
	public String getFax3() {
		return fax3;
	}

	@Index(name="firstNameIndex")
	@Size(min = 2)
	public String getFirstName() {
		return firstName;
	}

	@Transient
	public String getFirstNameOrEmailLocal() {
		if (!StringUtils.hasText(this.firstName)) {
			return this.getEmail1LocalPart();
		}
		return firstName;
	}

	public Gender getGender() {
		return gender;
	}

	public String getHobby() {
		return hobby;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	@Index(name="lastNameIndex")
	@Size(min = 2)
	public String getLastName() {
		return lastName;
	}

	public String getMaidenName() {
		return maidenName;
	}

	public String getMiddleName() {
		return middleName;
	}

	@Transient
	public String getName() {
		String middleStr = middleName == null ? "" : middleName + " ";

		if (StringUtils.hasText(firstName) && !StringUtils.hasText(lastName)) {
			return firstName;
		}

		if (!StringUtils.hasText(firstName) && StringUtils.hasText(lastName)) {
			return lastName;
		}

		return firstName + " " + middleStr + lastName;
	}

	@Index(name="nextCallDateIndex")
	public Date getNextCallDate() {
		return nextCallDate;
	}

	public String getNickname() {
		return nickname;
	}

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "contact")
	@javax.persistence.OrderBy("lastModifiedTime DESC")
	public List<Note> getNotes() {
		return notes;
	}

	public String getOccupation() {
		return occupation;
	}

	@Pattern(regexp = "^(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$", message = "Use format xxx-xxx-xxxx starting with 2 or higher starting with 2 or higher")
	public String getPhone1() {
		return phone1;
	}

	@Pattern(regexp = "^(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$", message = "Use format xxx-xxx-xxxx starting with 2 or higher starting with 2 or higher")
	public String getPhone2() {
		return phone2;
	}

	@Pattern(regexp = "^(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$", message = "Use format xxx-xxx-xxxx starting with 2 or higher starting with 2 or higher")
	public String getPhone3() {
		return phone3;
	}

	@Pattern(regexp = "^(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$", message = "Use format xxx-xxx-xxxx starting with 2 or higher starting with 2 or higher")
	public String getPhone4() {
		return phone4;
	}

	public Priority getPriority() {
		return priority;
	}

	public Sensitivity getSensitivity() {
		return sensitivity;
	}

	public String getShortName() {
		return shortName;
	}

	public String getSubject() {
		return subject;
	}

	public String getTags() {
		return tags;
	}

	public ContactType getType() {
		return type;
	}

	public String getWebsite() {
		return website;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public void setEmail1(String email1) {
		this.email1 = email1;
	}

	public void setEmail2(String email2) {
		this.email2 = email2;
	}

	public void setEmail3(String email3) {
		this.email3 = email3;
	}

	public void setFax1(String fax1) {
		this.fax1 = fax1;
	}

	public void setFax2(String fax2) {
		this.fax2 = fax2;
	}

	public void setFax3(String fax3) {
		this.fax3 = fax3;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public void setHobby(String hobby) {
		this.hobby = hobby;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setMaidenName(String maidenName) {
		this.maidenName = maidenName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public void setNextCallDate(Date nextCallDate) {
		this.nextCallDate = nextCallDate;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public void setNotes(List<Note> notes) {
		this.notes = notes;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	public void setPhone3(String phone3) {
		this.phone3 = phone3;
	}

	public void setPhone4(String phone4) {
		this.phone4 = phone4;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public void setSensitivity(Sensitivity sensitivity) {
		this.sensitivity = sensitivity;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public void setType(ContactType type) {
		this.type = type;
	}

	public void setWebsite(String website) {
		this.website = website;
	}
	public String getSpouse() {
		return spouse;
	}
	public void setSpouse(String spouse) {
		this.spouse = spouse;
	}
	public String getKidNames() {
		return kidNames;
	}
	public void setKidNames(String kidNames) {
		this.kidNames = kidNames;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getLinkedInProfile() {
		return linkedInProfile;
	}
	public void setLinkedInProfile(String linkedInProfile) {
		this.linkedInProfile = linkedInProfile;
	}
	public String getTwitterHandle() {
		return twitterHandle;
	}
	public void setTwitterHandle(String twitterHandle) {
		this.twitterHandle = twitterHandle;
	}
	
}
