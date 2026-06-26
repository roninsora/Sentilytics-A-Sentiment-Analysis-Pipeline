package roninsora.sentilytics.mapper.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import roninsora.sentilytics.mapper.Mapper;
import roninsora.sentilytics.models.dtos.SocialPostDTO;
import roninsora.sentilytics.models.entities.SocialPost;

@Component
public class SocialPostMapperImpl implements Mapper<SocialPost, SocialPostDTO> {

    private final ModelMapper modelMapper;

    public SocialPostMapperImpl(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    @Override
    public SocialPostDTO mapTo(SocialPost socialPost) {
        return modelMapper.map(socialPost, SocialPostDTO.class);
    }

    @Override
    public SocialPost mapFrom(SocialPostDTO socialPostDTO) {
        return modelMapper.map(socialPostDTO, SocialPost.class);
    }
}
