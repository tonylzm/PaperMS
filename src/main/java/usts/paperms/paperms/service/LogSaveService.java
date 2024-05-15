package usts.paperms.paperms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usts.paperms.paperms.Repository.LogRepository;
import usts.paperms.paperms.entity.Log;

@Service
public class LogSaveService {
    @Autowired
    private LogRepository logRepository;
    @Autowired
    private TimeService timeService;
    public void saveLog(String action , String actor) {
        Log logEntity = new Log();
        logEntity.setDate(timeService.getCurrentTime());
        logEntity.setAction(action);
        logEntity.setActor(actor);
        logRepository.save(logEntity);
    }
}
