package com.techtown.startui;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Calendar;

/* SERIALIZABLE/BUNDLE CLASS
 *
 * Serializable 클래스는 Activity 이동 시 객체 단위로 데이터를 주고 받을 때 사용하는 클래스입니다.
 * 기존의 기본 데이터 타입(e.i. int, string, etc.)을 전송할 때와는 달리 Caller 쪽 Activity에서
 * Bundle 이라는 클래스를 추가적으로 사용하여 객체를 전송합니다. 또한 Caller와 Callee 쪽 모두가
 * Serializable 용 메소드를 따로 사용하여 데이터를 전송하고 전달 받습니다.
 *
 * 각 Activity 클래스들에 대한 예시는 다음과 같습니다:
 *
 * [1] 보내는 쪽 Activity
 *
 * ClassRoomData classRoomData = new ClassRoomData([클래스 초기화]);
 * Intent intent = new Intent(getApplicationContext(), [받는 Activity 명].class);
 * Bundle bundle = new Bundle();  // 하등 신경쓰실 필요 없습니다.
 * bundle.putSerializable("classRoomData", classRoomData);  // 문자열로 되어있는 부분은 키 값으로, 굳이 인스턴스 명과 같을 필요는 없습니다. (***)
 * intent.putExtras(bundle);  // 기본 데이터 타입 전송 시와 동일합니다.
 * startActivity(intent);
 *
 *
 * [2] 받는 쪽 Activity
 *
 * Intent intent = getIntent();  // 받는 쪽은 새로운 Intent를 new 하지 않습니다. "기존에 보내진 Intent를 get 한다" (e.i. getIntent)라고 생각하시면 됩니다.
 * ClassRoomData classRoomData = (ClassRoomData)intent.getSerializableExtra("classRoomData");  // "classRoomData"를 키 값으로 보냈으니, "classRoomData"로 객체를 찾아옵니다.
 *
 * [***] - 설계 시 합의된 규칙이나 이름이 있을 때에는 당연히 예외입니다.
 *
 *
 * P.S. 클래스에 추가하고자 하는 필드가 있으실 때에는 이 파일 안에서는 왠만하면 주석을 달아주시면 감사하겠습니다.
 *      파일 입출력 및 본 어플리케이션 데이터 구조에 중추적인 역할을 할 클래스이고 모든 Activity가 공유하는
 *      클래스이기 때문입니다.
 *
 * */

@SuppressWarnings("serial")
public class ClassRoomData implements Serializable {

    private int userId;      // 사용자 계정 아이디(학번)
    private String userPw;      // 사용자 계정 비밀번호

    private Calendar calendar;   // 날짜 정보 전달을 위한 Java 기본 달력 객체

    private String userName;    // 사용자 이름
    private String phoneNumber; // 사용자 전화번호
    private String userEmail;   // 사용자 이메일
    private String classRoom;    // 예약 강의실 번호
    private long startTime;      // 이용 시작 시간
    private long endTime;        // 이용 종료 시간
    private String usage;        // 대여 사유

    /* 생성자 */
    public ClassRoomData() {                                                    // 기본 생성자
        this.userId = 0;
        this.userPw = "";

        this.calendar = null;

        this.userName = "";
        this.phoneNumber = "";
        this.userEmail = "";

        this.classRoom = "";
        this.startTime = 0;
        this.endTime = 0;
        this.usage = "";

    }
    public ClassRoomData(int userId, String userPw) {                      // 사용자 계정 정보 초기화 생성자
        this();     // 초기화
        this.userId = userId;
        this.userPw = userPw;
    }
    public ClassRoomData(Calendar calendar) { this.calendar = calendar; }       // 달력 객체 초기화 생성자


    /* 사용자 계정 관련 메소드 */
    public int getUserId() { return userId; }                                // 사용자 계정 아이디(학번) 반환
    public void setUserId(int userId) { this.userId = userId; }              // 사용자 계정 아이디(학번) 설정
    public String getUserPw() { return userPw; }                                // 사용자 계정 비밀번호 반환
    public void setUserPw(String userPw) { this.userPw = userPw; }              // 사용자 계정 비밀번호 설정


    /* 예약 정보 관련 메소드 */
    public String getClassRoom() { return classRoom; }                          // 예약 강의실 번호 반환
    public void setClassRoom(String classRoom) { this.classRoom = classRoom; }  // 예약 강의실 번호 설정
    public long getStartTime() { return startTime; }                            // 이용 시작 시간 반환
    public void setStartTime(long startTime) { this.startTime = startTime; }    // 이용 시작 시간 설정
    public long getEndTime() { return endTime; }                                // 이용 종료 시간 반환
    public void setEndTime(long endTime) { this.endTime = endTime; }            // 이용 종료 시간 설정
    public String getUsage() { return usage; }                                  // 대여 사유 반환
    public void setUsage(String usage) { this.usage = usage; }                  // 대여 사유 설정
    public String getUserName() { return userName; }                            // 사용자 이름 반환
    public void setUserName(String userName) { this.userName = userName; }       // 사용자 이름 설정
    public String getPhoneNumber() { return phoneNumber; }                      // 사용자 전화번호 반환
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }  // 사용자 전화번호 설정
    public String getUserEmail() { return userEmail; }                                  // 사용자 이메일 반환
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }          // 사용자 이메일 설정


    /* 달력 관련 메소드 */
    public void setCalendar(Calendar calendar) { this.calendar = calendar; }    // 달력 객체 설정
    public Calendar getCalendar() { return calendar; }                          // 달력 객체 반환
    public int getYear() { return calendar.get(Calendar.YEAR); }                // 해당 연도 반환
    public int getMonth() { return calendar.get(Calendar.MONTH); }              // 해당 월 반환 (범위가 0 ~ 11 이므로 +1 한 값이 실제 월 값이다.)
    public int getDate() { return calendar.get(Calendar.DAY_OF_MONTH); }        // 해당 일 반환
    public int getWeekDay() { return calendar.get(Calendar.DAY_OF_WEEK); }      // 해당 요일 반환 (1 ~ 7 사이 정수로 반환)
    public String getWeekDayKor() {                                             // 해당 요일을 한글로 반환
        String wkDayStr = null;
        switch (getWeekDay()) {
            case 1: wkDayStr = "일"; break; case 2: wkDayStr = "월"; break;
            case 3: wkDayStr = "화"; break; case 4: wkDayStr = "수"; break;
            case 5: wkDayStr = "목"; break; case 6: wkDayStr = "금"; break;
            case 7: wkDayStr = "토"; break;
        }
        return wkDayStr;
    }


    /* JSON 관련 메소드 */
    public JSONObject getJSON() {                                                   // 속성들을 JSON 형태로 변환 후 반환

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put("userId", this.userId);
            jsonObject.put("userPw", this.userPw);
            jsonObject.put("userName", this.userName);
            jsonObject.put("userEmail", this.userEmail);
            jsonObject.put("phoneNumber", this.phoneNumber);

            jsonObject.put("classRoom", this.classRoom);
            jsonObject.put("startTime", this.startTime);
            jsonObject.put("endTime", this.endTime);
            jsonObject.put("usage", this.usage);

        } catch (JSONException e) {

            e.printStackTrace();

        }

        return jsonObject;

    }
    public void readJSON(Context context, int useId) {                                                        // JSON 읽기

        //
        StringBuffer strBUFFER=new StringBuffer();
        try {
            InputStream is = new FileInputStream(context.getFilesDir().toString()+Integer.toString(userId)+".json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line = " ";
            while ((line = reader.readLine()) != null) {
                strBUFFER.append(line + "\n");
            }
            reader.close();
            is.close();

            JSONObject jo = new JSONObject();
            jo.getJSONObject(strBUFFER.toString());

            this.setUserId(jo.getInt("userId"));



        }catch(JSONException|IOException ex) {
            ex.printStackTrace();
        }

    }
    public void writeJSON(Context context) {                                                       // JSON 쓰기

        //
        String path = context.getFilesDir().toString();
        try {
            BufferedWriter bw=new BufferedWriter(new FileWriter(path + Integer.toString(this.getUserId())+".json"));
            JSONObject jo = new JSONObject();
            jo = getJSON();
            bw.write(jo.toString());
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}


