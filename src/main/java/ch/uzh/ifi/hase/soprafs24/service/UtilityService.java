package ch.uzh.ifi.hase.soprafs24.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

@Service
@Transactional
public class UtilityService {
    public void Assert(boolean expression,String msg) throws Exception{
        if(expression != true){
            throw new AssertionError(msg);
        }
    }
}
