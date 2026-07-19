package com.example.TaskService.service.mapper;

import com.example.TaskService.controller.request.subtask.CreateSubtaskRequest;
import com.example.TaskService.controller.request.subtask.UpdateSubtaskRequest;
import com.example.TaskService.controller.response.subtask.SubtaskCreatedResponse;
import com.example.TaskService.controller.response.subtask.SubtaskResponse;
import com.example.TaskService.controller.response.subtask.UpdatedSubtaskResponse;
import com.example.TaskService.data.entity.Subtask;
import com.example.TaskService.data.entity.Task;
import com.example.TaskService.service.dto.subtask.CreateSubtaskDto;
import com.example.TaskService.service.dto.subtask.SubtaskDto;
import com.example.TaskService.service.dto.subtask.UpdateSubtaskDto;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-14T23:31:33+0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from Maven%20, environment: Java 21.0.2 (Oracle Corporation)"
)
@Component
public class SubtaskMapperImpl implements SubtaskMapper {

    @Override
    public CreateSubtaskDto subtaskRequestToCreateSubtaskDto(CreateSubtaskRequest taskRequest) {
        if ( taskRequest == null ) {
            return null;
        }

        CreateSubtaskDto createSubtaskDto = new CreateSubtaskDto();

        createSubtaskDto.setSubtaskName( taskRequest.getSubtaskName() );
        createSubtaskDto.setDescription( taskRequest.getDescription() );
        createSubtaskDto.setEndTime( taskRequest.getEndTime() );
        createSubtaskDto.setTimeToSpend( taskRequest.getTimeToSpend() );

        return createSubtaskDto;
    }

    @Override
    public Subtask createSubtaskDtoToEntity(CreateSubtaskDto dto) {
        if ( dto == null ) {
            return null;
        }

        Subtask subtask = new Subtask();

        subtask.setSubtaskName( dto.getSubtaskName() );
        subtask.setDescription( dto.getDescription() );
        subtask.setEndTime( dto.getEndTime() );
        subtask.setTimeToSpend( dto.getTimeToSpend() );

        return subtask;
    }

    @Override
    public UpdateSubtaskDto updateSubtaskRequestToUpdateSubtaskDto(UpdateSubtaskRequest request) {
        if ( request == null ) {
            return null;
        }

        UpdateSubtaskDto updateSubtaskDto = new UpdateSubtaskDto();

        updateSubtaskDto.setSubtaskName( request.getSubtaskName() );
        updateSubtaskDto.setDescription( request.getDescription() );
        updateSubtaskDto.setEndTime( request.getEndTime() );
        updateSubtaskDto.setTimeToSpend( request.getTimeToSpend() );
        updateSubtaskDto.setIsComplete( request.getIsComplete() );

        return updateSubtaskDto;
    }

    @Override
    public SubtaskDto subtaskEntityToSubtaskDto(Subtask entity) {
        if ( entity == null ) {
            return null;
        }

        SubtaskDto subtaskDto = new SubtaskDto();

        subtaskDto.setTaskId( entityTaskId( entity ) );
        subtaskDto.setId( entity.getId() );
        subtaskDto.setSubtaskName( entity.getSubtaskName() );
        subtaskDto.setDescription( entity.getDescription() );
        subtaskDto.setCreatedTime( entity.getCreatedTime() );
        subtaskDto.setEndTime( entity.getEndTime() );
        subtaskDto.setTimeSpent( entity.getTimeSpent() );
        subtaskDto.setTimeToSpend( entity.getTimeToSpend() );
        subtaskDto.setIsComplete( entity.getIsComplete() );

        return subtaskDto;
    }

    @Override
    public List<SubtaskDto> subtaskEntityToSubtaskDto(List<Subtask> entities) {
        if ( entities == null ) {
            return null;
        }

        List<SubtaskDto> list = new ArrayList<SubtaskDto>( entities.size() );
        for ( Subtask subtask : entities ) {
            list.add( subtaskEntityToSubtaskDto( subtask ) );
        }

        return list;
    }

    @Override
    public UpdatedSubtaskResponse subtaskDtoToUpdatedSubtaskResponse(SubtaskDto dto) {
        if ( dto == null ) {
            return null;
        }

        UpdatedSubtaskResponse updatedSubtaskResponse = new UpdatedSubtaskResponse();

        updatedSubtaskResponse.setId( dto.getId() );
        updatedSubtaskResponse.setTaskId( dto.getTaskId() );
        updatedSubtaskResponse.setSubtaskName( dto.getSubtaskName() );
        updatedSubtaskResponse.setDescription( dto.getDescription() );
        updatedSubtaskResponse.setCreatedTime( dto.getCreatedTime() );
        updatedSubtaskResponse.setEndTime( dto.getEndTime() );
        updatedSubtaskResponse.setTimeToSpend( dto.getTimeToSpend() );
        updatedSubtaskResponse.setTimeSpent( dto.getTimeSpent() );
        updatedSubtaskResponse.setIsComplete( dto.getIsComplete() );

        return updatedSubtaskResponse;
    }

    @Override
    public SubtaskCreatedResponse subtaskDtoToCreatedResponse(SubtaskDto dto) {
        if ( dto == null ) {
            return null;
        }

        SubtaskCreatedResponse subtaskCreatedResponse = new SubtaskCreatedResponse();

        subtaskCreatedResponse.setId( dto.getId() );
        subtaskCreatedResponse.setTaskId( dto.getTaskId() );
        subtaskCreatedResponse.setCreatedTime( dto.getCreatedTime() );

        return subtaskCreatedResponse;
    }

    @Override
    public SubtaskResponse subtaskDtoToResponse(SubtaskDto dto) {
        if ( dto == null ) {
            return null;
        }

        SubtaskResponse subtaskResponse = new SubtaskResponse();

        subtaskResponse.setId( dto.getId() );
        subtaskResponse.setTaskId( dto.getTaskId() );
        subtaskResponse.setSubtaskName( dto.getSubtaskName() );
        subtaskResponse.setDescription( dto.getDescription() );
        subtaskResponse.setCreatedTime( dto.getCreatedTime() );
        subtaskResponse.setEndTime( dto.getEndTime() );
        subtaskResponse.setTimeToSpend( dto.getTimeToSpend() );
        subtaskResponse.setTimeSpent( dto.getTimeSpent() );
        subtaskResponse.setIsComplete( dto.getIsComplete() );

        return subtaskResponse;
    }

    @Override
    public List<SubtaskResponse> subtaskDtoToSubtaskResponse(List<SubtaskDto> dtos) {
        if ( dtos == null ) {
            return null;
        }

        List<SubtaskResponse> list = new ArrayList<SubtaskResponse>( dtos.size() );
        for ( SubtaskDto subtaskDto : dtos ) {
            list.add( subtaskDtoToResponse( subtaskDto ) );
        }

        return list;
    }

    @Override
    public void updateSubtaskFromDto(UpdateSubtaskDto dto, Subtask subtask) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getSubtaskName() != null ) {
            subtask.setSubtaskName( dto.getSubtaskName() );
        }
        if ( dto.getDescription() != null ) {
            subtask.setDescription( dto.getDescription() );
        }
        if ( dto.getEndTime() != null ) {
            subtask.setEndTime( dto.getEndTime() );
        }
        if ( dto.getTimeToSpend() != null ) {
            subtask.setTimeToSpend( dto.getTimeToSpend() );
        }
        if ( dto.getIsComplete() != null ) {
            subtask.setIsComplete( dto.getIsComplete() );
        }
    }

    private Long entityTaskId(Subtask subtask) {
        Task task = subtask.getTask();
        if ( task == null ) {
            return null;
        }
        return task.getId();
    }
}
