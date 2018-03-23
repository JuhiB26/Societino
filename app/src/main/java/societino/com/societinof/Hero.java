package societino.com.societinof;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.firestore.DocumentSnapshot;

public class Hero implements Parcelable{

    protected Hero(Parcel in) {
        socName = in.readString();
        userType = in.readString();
        uid = in.readString();
        address=in.readString();
        number=in.readString();
        profession=in.readString();
        code=in.readString();
        imgUrl=in.readString();
        uname=in.readString();


    }

    public static final Creator<Hero> CREATOR = new Creator<Hero>() {
        @Override
        public Hero createFromParcel(Parcel in) {
            return new Hero(in);
        }

        @Override
        public Hero[] newArray(int size) {
            return new Hero[size];
        }
    };

    public Hero() {

    }

    public String getSocName() {
        return socName;
    }

    public void setSocName(String socName) {
        this.socName = socName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    String socName;
    String userType;
    String uid;

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    String uname;
    String address;
    String number;
    String profession;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    String imgUrl;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    String code;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(socName);
        parcel.writeString(userType);
        parcel.writeString(uid);
        parcel.writeString(address);
        parcel.writeString(number);
        parcel.writeString(profession);
        parcel.writeString(code);
        parcel.writeString(imgUrl);
        parcel.writeString(uname);
    }
}