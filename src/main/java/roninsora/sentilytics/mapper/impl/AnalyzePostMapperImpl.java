package roninsora.sentilytics.mapper.impl;

import org.modelmapper.ModelMapper;
import roninsora.sentilytics.mapper.Mapper;
import roninsora.sentilytics.models.dtos.AnalyzePostDTO;
import roninsora.sentilytics.models.entities.AnalyzePost;

public class AnalyzePostMapperImpl implements Mapper<AnalyzePost, AnalyzePostDTO> {

    private final ModelMapper modelMapper;

    public AnalyzePostMapperImpl(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    @Override
    public AnalyzePostDTO mapTo(AnalyzePost analyzePost) {
        return modelMapper.map(analyzePost, AnalyzePostDTO.class);
    }

    @Override
    public AnalyzePost mapFrom(AnalyzePostDTO analyzePostDTO) {
        return modelMapper.map(analyzePostDTO, AnalyzePost.class);
    }
}
