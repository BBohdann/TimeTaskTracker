package com.example.TaskService.service.mapper;

import com.example.TaskService.controller.request.CreateSubtaskRequest;
import com.example.TaskService.controller.request.UpdateSubtaskRequest;
import com.example.TaskService.controller.responce.SubtaskResponce;
import com.example.TaskService.data.entity.Subtask;
import com.example.TaskService.data.entity.Task;
import com.example.TaskService.service.dto.CreateSubtaskDto;
import com.example.TaskService.service.dto.SubtaskDto;
import org.springframework.stereotype.Component;

@Component
public class SubtaskMapper {
    public CreateSubtaskDto subtaskRequestToDto(CreateSubtaskRequest taskRequest){
        CreateSubtaskDto dto = new CreateSubtaskDto();
        dto.setTaskId(taskRequest.getTaskId());
        dto.setSubtaskName(taskRequest.getSubtaskName());
        dto.setEndTime(taskRequest.getEndTime());
        dto.setDescription(taskRequest.getDescription());
        dto.setTimeToSpend(taskRequest.getTimeToSpend());
        return dto;
    }

    public Subtask createSubtaskDtoToEntity(CreateSubtaskDto dto){
        Task task = new Task();
        task.setId(dto.getTaskId());

        Subtask entity = new Subtask();
        entity.setTask(task);
        entity.setCreatedTime(dto.getCreatedTime());
        entity.setSubtaskName(dto.getSubtaskName());
        entity.setEndTime(dto.getEndTime());
        entity.setDescription(dto.getDescription());
        entity.setTimeToSpend(dto.getTimeToSpend());
        return entity;
    }

    public SubtaskDto entityToSubtaskDto(Subtask entity) {
        SubtaskDto dto = new SubtaskDto();
        dto.setId(entity.getId());
        dto.setTaskId(entity.getTask().getId());
        dto.setSubtaskName(entity.getSubtaskName());
        dto.setCreatedTime(entity.getCreatedTime());
        dto.setEndTime(entity.getEndTime());
        dto.setDescription(entity.getDescription());
        dto.setTimeToSpend(entity.getTimeToSpend());
        dto.setTimeSpent(entity.getTimeSpent());
        dto.setIsComplete(entity.getIsComplete());
        return dto;
    }

    public SubtaskDto updateSubtaskRequestToSubtaskDto(UpdateSubtaskRequest request){
        SubtaskDto dto = new SubtaskDto();
        dto.setId(request.getId());
        dto.setSubtaskName(request.getSubtaskName());
        dto.setEndTime(request.getEndTime());
        dto.setDescription(request.getDescription());
        dto.setTimeToSpend(request.getTimeToSpend());
        dto.setIsComplete(request.getIsComplete());
        return dto;
    }

    public SubtaskResponce subtaskDtoToResponce(SubtaskDto dto){
        SubtaskResponce responce = new SubtaskResponce();
        responce.setId(dto.getId());
        responce.setTaskId(dto.getTaskId());
        responce.setCreatedTime(dto.getCreatedTime());
        return responce;
    }
}
