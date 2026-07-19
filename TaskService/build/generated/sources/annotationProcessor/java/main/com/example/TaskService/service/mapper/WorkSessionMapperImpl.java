package com.example.TaskService.service.mapper;

import com.example.TaskService.controller.request.session.StartSessionRequest;
import com.example.TaskService.controller.response.session.FinishSessionResponse;
import com.example.TaskService.controller.response.session.SessionResponse;
import com.example.TaskService.controller.response.session.StartSessionResponse;
import com.example.TaskService.data.entity.WorkSession;
import com.example.TaskService.service.dto.session.FinishSessionDto;
import com.example.TaskService.service.dto.session.StartSessionDto;
import com.example.TaskService.service.dto.session.WorkSessionDto;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-06T02:11:35+0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from Maven%20, environment: Java 21.0.2 (Oracle Corporation)"
)
@Component
public class WorkSessionMapperImpl implements WorkSessionMapper {

    @Override
    public WorkSession startSessionDtoToSessionEntity(StartSessionDto dto) {
        if ( dto == null ) {
            return null;
        }

        WorkSession workSession = new WorkSession();

        workSession.setUserId( dto.getUserId() );
        workSession.setTaskId( dto.getTaskId() );
        workSession.setSubtaskId( dto.getSubtaskId() );

        return workSession;
    }

    @Override
    public StartSessionDto toStartSessionDto(StartSessionRequest request, Long userId) {
        if ( request == null && userId == null ) {
            return null;
        }

        StartSessionDto startSessionDto = new StartSessionDto();

        if ( request != null ) {
            startSessionDto.setTaskId( request.getTaskId() );
            startSessionDto.setSubtaskId( request.getSubtaskId() );
        }
        startSessionDto.setUserId( userId );

        return startSessionDto;
    }

    @Override
    public WorkSessionDto sessionEntityToSessionDto(WorkSession entity) {
        if ( entity == null ) {
            return null;
        }

        WorkSessionDto workSessionDto = new WorkSessionDto();

        workSessionDto.setId( entity.getId() );
        workSessionDto.setTaskId( entity.getTaskId() );
        workSessionDto.setSubtaskId( entity.getSubtaskId() );
        workSessionDto.setStatus( entity.getStatus() );

        return workSessionDto;
    }

    @Override
    public List<WorkSessionDto> sessionEntityToSessionDto(List<WorkSession> entities) {
        if ( entities == null ) {
            return null;
        }

        List<WorkSessionDto> list = new ArrayList<WorkSessionDto>( entities.size() );
        for ( WorkSession workSession : entities ) {
            list.add( sessionEntityToSessionDto( workSession ) );
        }

        return list;
    }

    @Override
    public List<SessionResponse> sessionDtoToSessionResponse(List<WorkSessionDto> dtos) {
        if ( dtos == null ) {
            return null;
        }

        List<SessionResponse> list = new ArrayList<SessionResponse>( dtos.size() );
        for ( WorkSessionDto workSessionDto : dtos ) {
            list.add( workSessionDtoToSessionResponse( workSessionDto ) );
        }

        return list;
    }

    @Override
    public StartSessionResponse startSessionDtoToStartSessionResponse(WorkSessionDto dto) {
        if ( dto == null ) {
            return null;
        }

        StartSessionResponse startSessionResponse = new StartSessionResponse();

        startSessionResponse.setId( dto.getId() );
        startSessionResponse.setTaskId( dto.getTaskId() );
        startSessionResponse.setSubtaskId( dto.getSubtaskId() );
        startSessionResponse.setStatus( dto.getStatus() );

        return startSessionResponse;
    }

    @Override
    public FinishSessionDto sessionEntityToFinishSessionDto(WorkSession entity, Integer duration) {
        if ( entity == null && duration == null ) {
            return null;
        }

        FinishSessionDto finishSessionDto = new FinishSessionDto();

        if ( entity != null ) {
            finishSessionDto.setId( entity.getId() );
            finishSessionDto.setTaskId( entity.getTaskId() );
            finishSessionDto.setSubtaskId( entity.getSubtaskId() );
            finishSessionDto.setStatus( entity.getStatus() );
        }
        finishSessionDto.setDuration( duration );

        return finishSessionDto;
    }

    @Override
    public FinishSessionResponse finishSessionDtoToFinishSessionResponse(FinishSessionDto dto) {
        if ( dto == null ) {
            return null;
        }

        FinishSessionResponse finishSessionResponse = new FinishSessionResponse();

        finishSessionResponse.setId( dto.getId() );
        finishSessionResponse.setTaskId( dto.getTaskId() );
        finishSessionResponse.setSubtaskId( dto.getSubtaskId() );
        finishSessionResponse.setDuration( dto.getDuration() );
        finishSessionResponse.setStatus( dto.getStatus() );

        return finishSessionResponse;
    }

    protected SessionResponse workSessionDtoToSessionResponse(WorkSessionDto workSessionDto) {
        if ( workSessionDto == null ) {
            return null;
        }

        SessionResponse sessionResponse = new SessionResponse();

        sessionResponse.setId( workSessionDto.getId() );
        sessionResponse.setTaskId( workSessionDto.getTaskId() );
        sessionResponse.setSubtaskId( workSessionDto.getSubtaskId() );
        sessionResponse.setStatus( workSessionDto.getStatus() );

        return sessionResponse;
    }
}
