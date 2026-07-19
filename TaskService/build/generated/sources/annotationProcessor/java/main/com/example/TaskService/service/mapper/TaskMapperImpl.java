package com.example.TaskService.service.mapper;

import com.example.TaskService.controller.request.task.CreateTaskRequest;
import com.example.TaskService.controller.request.task.UpdateTaskRequest;
import com.example.TaskService.controller.response.task.TaskCreatedResponse;
import com.example.TaskService.controller.response.task.TaskResponse;
import com.example.TaskService.controller.response.task.TaskUpdatedResponse;
import com.example.TaskService.data.entity.Subtask;
import com.example.TaskService.data.entity.Task;
import com.example.TaskService.service.dto.task.CreateTaskDto;
import com.example.TaskService.service.dto.task.SubtaskMainInfoDto;
import com.example.TaskService.service.dto.task.TaskDto;
import com.example.TaskService.service.dto.task.TaskWithSubtasksDto;
import com.example.TaskService.service.dto.task.UpdateTaskDto;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-14T23:31:38+0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from Maven%20, environment: Java 21.0.2 (Oracle Corporation)"
)
@Component
public class TaskMapperImpl implements TaskMapper {

    @Override
    public TaskWithSubtasksDto taskEntityToTaskWithSubtasksDto(Task entity) {
        if ( entity == null ) {
            return null;
        }

        TaskWithSubtasksDto taskWithSubtasksDto = new TaskWithSubtasksDto();

        taskWithSubtasksDto.setId( entity.getId() );
        taskWithSubtasksDto.setTaskName( entity.getTaskName() );
        taskWithSubtasksDto.setDescription( entity.getDescription() );
        taskWithSubtasksDto.setCreatedTime( entity.getCreatedTime() );
        taskWithSubtasksDto.setEndTime( entity.getEndTime() );
        taskWithSubtasksDto.setTimeSpent( entity.getTimeSpent() );
        taskWithSubtasksDto.setTimeToSpend( entity.getTimeToSpend() );
        taskWithSubtasksDto.setIsComplete( entity.getIsComplete() );
        taskWithSubtasksDto.setSubtasks( subtaskListToSubtaskMainInfoDtoList( entity.getSubtasks() ) );

        return taskWithSubtasksDto;
    }

    @Override
    public TaskDto taskEntityToTaskDto(Task entity) {
        if ( entity == null ) {
            return null;
        }

        TaskDto taskDto = new TaskDto();

        taskDto.setId( entity.getId() );
        taskDto.setTaskName( entity.getTaskName() );
        taskDto.setDescription( entity.getDescription() );
        taskDto.setCreatedTime( entity.getCreatedTime() );
        taskDto.setEndTime( entity.getEndTime() );
        taskDto.setTimeSpent( entity.getTimeSpent() );
        taskDto.setTimeToSpend( entity.getTimeToSpend() );
        taskDto.setIsComplete( entity.getIsComplete() );

        return taskDto;
    }

    @Override
    public List<TaskWithSubtasksDto> taskEntityToTaskWithSubtasksDto(Collection<Task> entities) {
        if ( entities == null ) {
            return null;
        }

        List<TaskWithSubtasksDto> list = new ArrayList<TaskWithSubtasksDto>( entities.size() );
        for ( Task task : entities ) {
            list.add( taskEntityToTaskWithSubtasksDto( task ) );
        }

        return list;
    }

    @Override
    public CreateTaskDto taskRequestToCreateTaskDto(CreateTaskRequest taskRequest, Long userId) {
        if ( taskRequest == null && userId == null ) {
            return null;
        }

        CreateTaskDto createTaskDto = new CreateTaskDto();

        if ( taskRequest != null ) {
            createTaskDto.setTaskName( taskRequest.getTaskName() );
            createTaskDto.setEndTime( taskRequest.getEndTime() );
            createTaskDto.setDescription( taskRequest.getDescription() );
            createTaskDto.setTimeToSpend( taskRequest.getTimeToSpend() );
        }
        createTaskDto.setUserId( userId );

        return createTaskDto;
    }

    @Override
    public Task createTaskToTaskEntity(CreateTaskDto createTaskDto) {
        if ( createTaskDto == null ) {
            return null;
        }

        Task task = new Task();

        task.setUserId( createTaskDto.getUserId() );
        task.setTaskName( createTaskDto.getTaskName() );
        task.setDescription( createTaskDto.getDescription() );
        task.setCreatedTime( createTaskDto.getCreatedTime() );
        task.setEndTime( createTaskDto.getEndTime() );
        task.setTimeToSpend( createTaskDto.getTimeToSpend() );

        return task;
    }

    @Override
    public TaskCreatedResponse taskDtoToTaskCreatedResponse(TaskDto dto) {
        if ( dto == null ) {
            return null;
        }

        TaskCreatedResponse taskCreatedResponse = new TaskCreatedResponse();

        taskCreatedResponse.setId( dto.getId() );
        taskCreatedResponse.setCreatedTime( dto.getCreatedTime() );

        return taskCreatedResponse;
    }

    @Override
    public TaskUpdatedResponse taskDtoToTaskUpdatedResponse(TaskDto dto) {
        if ( dto == null ) {
            return null;
        }

        TaskUpdatedResponse taskUpdatedResponse = new TaskUpdatedResponse();

        taskUpdatedResponse.setId( dto.getId() );
        taskUpdatedResponse.setTaskName( dto.getTaskName() );
        taskUpdatedResponse.setDescription( dto.getDescription() );
        taskUpdatedResponse.setCreatedTime( dto.getCreatedTime() );
        taskUpdatedResponse.setEndTime( dto.getEndTime() );
        taskUpdatedResponse.setTimeToSpend( dto.getTimeToSpend() );
        taskUpdatedResponse.setTimeSpent( dto.getTimeSpent() );
        taskUpdatedResponse.setIsComplete( dto.getIsComplete() );

        return taskUpdatedResponse;
    }

    @Override
    public TaskResponse taskWithSubtasksDtoToTaskResponse(TaskWithSubtasksDto dto) {
        if ( dto == null ) {
            return null;
        }

        TaskResponse taskResponse = new TaskResponse();

        taskResponse.setId( dto.getId() );
        taskResponse.setTaskName( dto.getTaskName() );
        taskResponse.setDescription( dto.getDescription() );
        taskResponse.setCreatedTime( dto.getCreatedTime() );
        taskResponse.setEndTime( dto.getEndTime() );
        taskResponse.setTimeToSpend( dto.getTimeToSpend() );
        taskResponse.setTimeSpent( dto.getTimeSpent() );
        taskResponse.setIsComplete( dto.getIsComplete() );
        List<SubtaskMainInfoDto> list = dto.getSubtasks();
        if ( list != null ) {
            taskResponse.setSubtasks( new ArrayList<SubtaskMainInfoDto>( list ) );
        }

        return taskResponse;
    }

    @Override
    public UpdateTaskDto updateTaskRequestToUpdateTaskDto(UpdateTaskRequest request) {
        if ( request == null ) {
            return null;
        }

        UpdateTaskDto updateTaskDto = new UpdateTaskDto();

        updateTaskDto.setTaskName( request.getTaskName() );
        updateTaskDto.setDescription( request.getDescription() );
        updateTaskDto.setEndTime( request.getEndTime() );
        updateTaskDto.setTimeToSpend( request.getTimeToSpend() );
        updateTaskDto.setIsComplete( request.getIsComplete() );

        return updateTaskDto;
    }

    @Override
    public List<TaskResponse> taskWithSubtaskDtoToTaskResponse(List<TaskWithSubtasksDto> dtos) {
        if ( dtos == null ) {
            return null;
        }

        List<TaskResponse> list = new ArrayList<TaskResponse>( dtos.size() );
        for ( TaskWithSubtasksDto taskWithSubtasksDto : dtos ) {
            list.add( taskWithSubtasksDtoToTaskResponse( taskWithSubtasksDto ) );
        }

        return list;
    }

    @Override
    public void updateTaskFromDto(UpdateTaskDto dto, Task task) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getTaskName() != null ) {
            task.setTaskName( dto.getTaskName() );
        }
        if ( dto.getDescription() != null ) {
            task.setDescription( dto.getDescription() );
        }
        if ( dto.getEndTime() != null ) {
            task.setEndTime( dto.getEndTime() );
        }
        if ( dto.getTimeToSpend() != null ) {
            task.setTimeToSpend( dto.getTimeToSpend() );
        }
        if ( dto.getIsComplete() != null ) {
            task.setIsComplete( dto.getIsComplete() );
        }
    }

    protected SubtaskMainInfoDto subtaskToSubtaskMainInfoDto(Subtask subtask) {
        if ( subtask == null ) {
            return null;
        }

        SubtaskMainInfoDto subtaskMainInfoDto = new SubtaskMainInfoDto();

        subtaskMainInfoDto.setId( subtask.getId() );
        subtaskMainInfoDto.setSubtaskName( subtask.getSubtaskName() );
        subtaskMainInfoDto.setTimeSpent( subtask.getTimeSpent() );
        subtaskMainInfoDto.setTimeToSpend( subtask.getTimeToSpend() );
        subtaskMainInfoDto.setIsComplete( subtask.getIsComplete() );
        subtaskMainInfoDto.setEndTime( subtask.getEndTime() );

        return subtaskMainInfoDto;
    }

    protected List<SubtaskMainInfoDto> subtaskListToSubtaskMainInfoDtoList(List<Subtask> list) {
        if ( list == null ) {
            return null;
        }

        List<SubtaskMainInfoDto> list1 = new ArrayList<SubtaskMainInfoDto>( list.size() );
        for ( Subtask subtask : list ) {
            list1.add( subtaskToSubtaskMainInfoDto( subtask ) );
        }

        return list1;
    }
}
