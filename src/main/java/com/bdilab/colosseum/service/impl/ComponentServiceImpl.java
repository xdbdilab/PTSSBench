package com.bdilab.colosseum.service.impl;

import com.bdilab.colosseum.domain.Component;
import com.bdilab.colosseum.domain.ComponentParam;
import com.bdilab.colosseum.domain.workflow.Parameter;
import com.bdilab.colosseum.mapper.ComponentMapper;
import com.bdilab.colosseum.mapper.ComponentParamMapper;
import com.bdilab.colosseum.service.ComponentService;
import com.bdilab.colosseum.utils.FileUtils;
import com.bdilab.colosseum.vo.ComponentVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;

/**
 * @Author duyeye
 * @Date 2020/12/29 0029 15:42
 */
@Service
public class ComponentServiceImpl implements ComponentService {

    @Autowired
    ComponentMapper componentMapper;

    @Autowired
    ComponentParamMapper componentParamMapper;

    @Value("${logo.path}")
    private String logoPath;

    @Value("${image.map}")
    private String imageMap;

    @Override
    public List<ComponentVO> loadPublicComponent() {
        List<ComponentVO> result = new LinkedList<>();
        List<Component> components = componentMapper.selectAllPublicComponent();
        for (Component component:components){
            ComponentVO componentVO = buildComponentVO(component);
            if(!componentVO.getLogo_path().equals("")){
                componentVO.setLogo_path(imageMap+componentVO.getLogo_path());
            }
            result.add(componentVO);
        }
        return result;
    }

    @Override
    public boolean createComponent(String name,  Byte type, String desc, String image, List<ComponentParam> param, long userId, String  componentFile, MultipartFile logo) {

        //如果未上传logo，使用默认logo
        boolean uploadSuccess=true;
        String path="";
        if(logo!=null){
            String logoPath1=this.logoPath+"admin";
            String[] strs=logo.getOriginalFilename().split("\\.");
            String fileName="systemComponent_"+UUID.randomUUID() +"."+strs[strs.length-1];
            uploadSuccess=FileUtils.uploadFileRename(logo,logoPath1,fileName);
            path=logoPath1+File.separator+fileName;
        }
        if(uploadSuccess){
            Component component = new Component();
            component.setType(type);
            component.setComponentName(name);
            component.setDescription(desc);
            component.setFkUserId(userId);
            component.setImage(image);
            component.setCreateTime(new Date());
            component.setComponentFile(componentFile);
            component.setLogoPath(path);
            componentMapper.insert(component);

            //将该组件的参数插入到组件参数表中
            if(param!=null){
                for (ComponentParam componentParam:param){
                    componentParam.setFkComponentId(component.getId());
                    componentParam.setId(null);
                    componentParamMapper.insert(componentParam);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteComponentById(Long componentId) {
        //删除组件参数
        List<ComponentParam> componentParams = componentParamMapper.selectByComponentId(componentId);
        for (ComponentParam cp:componentParams){
            componentParamMapper.deleteByPrimaryKey(cp.getId());
        }
        //删除组件
        Component component=componentMapper.selectByPrimaryKey(componentId);
        String path=component.getLogoPath();
        if(!path.equals("")){
            File file = new File(path);
            file.delete();
        }
        componentMapper.deleteByPrimaryKey(componentId);
        return true;
    }

    @Override
    public Map<String, Object> selectUserComponent(int pageNum, int pageSize, Long userId) {
        Map<String, Object> data = new HashMap<>();
        PageHelper.startPage(pageNum,pageSize);
        List<Component> components = componentMapper.selectByUserId(userId);
        List<ComponentVO> componentVOS = new LinkedList<>();
        for (Component component:components){
            if(component.getLogoPath().equals("")){
                component.setLogoPath(imageMap+component.getLogoPath());
            }
            componentVOS.add(buildComponentVO(component));
        }
        PageInfo pageInfo = new PageInfo(componentVOS);
        data.put("component list",componentVOS);
        data.put("pageNum",pageInfo.getPageNum());
        data.put("total",pageInfo.getTotal());
        return data;
    }

    ComponentVO buildComponentVO(Component component){
        ComponentVO componentVO = new ComponentVO();
        componentVO.setId(component.getId());
        componentVO.setType(component.getType());
        componentVO.setComponentName(component.getComponentName());
        componentVO.setDescription(component.getDescription());
        componentVO.setLogo_path(component.getLogoPath());
        List<Parameter> parameters = new LinkedList<>();
        List<ComponentParam> componentParams = componentParamMapper.selectByComponentId(component.getId());
        for (ComponentParam param:componentParams){
            Parameter parameter = new Parameter();
            parameter.setParamName(param.getParamName());
            parameter.setFkComponentId(param.getFkComponentId());
            parameter.setParamType(param.getParamType());
            parameter.setParamDesc(param.getParamDesc());
            if (param.getParamType().equals("enum")){
                String[] enumValue = param.getDefaultValue().split(",");
                parameter.setEnumValue(enumValue);
            }else {
                if (param.getParamType().equals("int")){
                    parameter.setDefaultValue(Integer.parseInt(param.getDefaultValue()));
                }else if (param.getParamType().equals("double")){
                    parameter.setDefaultValue(Double.parseDouble(param.getDefaultValue()));
                }else {
                    parameter.setDefaultValue(param.getDefaultValue());
                }
            }
            parameters.add(parameter);
        }
        componentVO.setParameters(parameters);
        return componentVO;
    }

}
