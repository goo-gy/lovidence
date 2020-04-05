package com.example.lovidence.data;

import com.example.lovidence.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 * 로그인 자격 증명으로 인증을 처리하고 사용자 정보를 검색하는 클래스입니다.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication(사용자 인증)
            if(!username.equals("myid123") || !password.equals("password")){return new Result.Fail();}
            //아이디 목록에서 확인후 유효한 값이면 fakeUser에 넘겨줌
            //현재 있는 Error는 Exception이 발생했을떄 동작하는코드
            //즉, 문자열 비교할때는 Exception을 사용하지않도록
            LoggedInUser fakeUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            "Jane Doe");
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
