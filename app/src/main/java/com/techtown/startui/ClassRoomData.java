package com.techtown.startui;

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

    private Calendar calendar;   // 날짜 정보 전달을 위한 Java 기본 달력 객체

    /* 생성자 */
    public ClassRoomData() {
        // 기본 생성자
    }
    public ClassRoomData(Calendar calendar) { this.calendar = calendar; }       // 달력 객체 초기화

    /* 달력 관련 메소드 */
    public void setCalendar(Calendar calendar) { this.calendar = calendar; }    // 달력 객체 설정
    public Calendar getCalendar() { return calendar; }                          // 달력 객체 반환
    public int getYear() { return calendar.get(Calendar.YEAR); }               // 해당 연도 반환
    public int getMonth() { return calendar.get(Calendar.MONTH); }             // 해당 월 반환 (범위가 0 ~ 11 이므로 +1 한 값이 실제 월 값이다.)
    public int getDate() { return calendar.get(Calendar.DAY_OF_MONTH); }      // 해당 일 반환
    public int getWeekDay() { return calendar.get(Calendar.DAY_OF_WEEK); }    // 해당 요일 반환 (1 ~ 7 사이 정수로 반환)
    public String getWeekDayKor() {                                                // 해당 요일을 한글로 반환
        String wkDayStr = null;
        switch (getWeekDay()) {
            case 1: wkDayStr = "일"; break; case 2: wkDayStr = "월"; break;
            case 3: wkDayStr = "화"; break; case 4: wkDayStr = "수"; break;
            case 5: wkDayStr = "목"; break; case 6: wkDayStr = "금"; break;
            case 7: wkDayStr = "토"; break;
        }
        return wkDayStr;
    }

}
