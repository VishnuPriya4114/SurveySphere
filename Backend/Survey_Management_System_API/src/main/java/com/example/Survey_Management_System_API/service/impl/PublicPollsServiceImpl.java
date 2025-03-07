package com.example.Survey_Management_System_API.service.impl;

import com.example.Survey_Management_System_API.dto.PublicPollsDTO;
import com.example.Survey_Management_System_API.entity.PublicPolls;
import com.example.Survey_Management_System_API.repository.PublicPollsRepository;
import com.example.Survey_Management_System_API.service.PublicPollsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PublicPollsServiceImpl implements PublicPollsService {
    @Autowired
    private PublicPollsRepository publicPollsRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PublicPollsDTO createPublicPoll(PublicPollsDTO publicPollsDTO) {
        // Check if the user has already responded to this poll
        PublicPolls existingPoll = publicPollsRepository.findByPollIdAndPublicId(publicPollsDTO.getPollId(), publicPollsDTO.getPublicId());
        if (existingPoll != null) {
            throw new RuntimeException("You have already submitted a response for this poll.");
        }

        PublicPolls publicPolls = modelMapper.map(publicPollsDTO, PublicPolls.class);
        publicPolls = publicPollsRepository.save(publicPolls);
        return modelMapper.map(publicPolls, PublicPollsDTO.class);
    }

    @Override
    public List<PublicPollsDTO> getAllPublicPolls() {
        return publicPollsRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PublicPollsDTO> getPublicPollsByPollId(Long pollId) {
        return publicPollsRepository.findByPollId(pollId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PublicPollsDTO> getPublicPollsByPublicId(Long publicId) {
        List<PublicPolls> polls = publicPollsRepository.findByPublicId(publicId);
        return polls.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    private PublicPollsDTO convertToDTO(PublicPolls publicPolls) {
        return modelMapper.map(publicPolls, PublicPollsDTO.class);
    }
    
    @Override
    public Map<String, Long> countVotesByCategory(Long publicId) {
        List<Object[]> results = publicPollsRepository.countVotesByCategory(publicId);
        return results.stream()
                      .collect(Collectors.toMap(
                          result -> (String) result[0], 
                          result -> (Long) result[1]
                      ));
}
}
