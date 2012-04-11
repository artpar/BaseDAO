package Usage;

import Example.StationDAO;
import Example.StationDTO;

public class test
{
	public static void main(String[] args){
	
		StationDAO sDAO = new StationDAO();
		StationDTO sDTO = new StationDTO();
		
		sDTO.setId("5");
		sDTO = sDAO.selectById(sDTO);
		
		sDTO.setName("new name");
		sDAO.updateById(sDTO, Arrays.asList("name"));
		
	}
}
