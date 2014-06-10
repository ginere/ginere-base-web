package eu.ginere.base.web.menu;

import java.util.ArrayList;
import java.util.List;

import eu.ginere.base.util.descriptor.annotation.Description;


public class MenuItem {

	private static final List<MenuItem> EMPTY_ITEM_LIST=new ArrayList<MenuItem>(0);
	
	@Description(description="The label to show")
	private final String label;
	@Description(description="The uniq Id")
	private final String id;

	@Description(description="The right that the user must have to show the menu")
	private final String right;
	@Description(description="The menu URL")
	private final String url;
	@Description(description="The menu css style width the icon")
	private final String cls;
	@Description(description=" ???")
	private final boolean window;
	
	private final List<MenuItem> itemList;
	
	@Description(description="If this is true the menu is a separator")
	private final boolean separator;
	
	
 	public MenuItem(String id,String label, String right, String url,String cls,boolean isSeparator, boolean window){
 		this.label=label;
 		this.id = id;
 		this.right=right;
 		this.url=url;
 		this.cls=cls;
		this.separator=isSeparator;
		this.window=this.window;
 		this.itemList=EMPTY_ITEM_LIST;
 	}
 	
 	MenuItem(String id,String label, String right, String url,String cls,boolean isSeparator, boolean window, List<MenuItem> itemList){
 		this.label=label;
 		this.id = id;
 		this.right=right;
 		this.url=url;
 		this.cls=cls;
		this.separator=isSeparator;
 		this.itemList=itemList;
 		this.window=window;
 	}
 	
 	MenuItem(MenuItem orig){
 		this.id = orig.getId();
 		this.label=orig.getLabel();
 		this.right=orig.getRight();
 		this.url=orig.getUrl();
 		this.cls=orig.getCls();
 		this.separator=orig.isSeparator();
 		this.itemList=EMPTY_ITEM_LIST;
 		this.window=orig.isWindow();
 	}
 	
 	MenuItem(MenuItem orig,List<MenuItem> itemList){
 		this.id = orig.getId();
 		this.label=orig.getLabel();
 		this.right=orig.getRight();
 		this.url=orig.getUrl();
 		this.cls=orig.getCls();
 		this.window=orig.isWindow();
 		this.separator=orig.isSeparator();
 		this.itemList=itemList;
 	}
	
 	public String getLabel(){
 		return label;
 	}
 	public String getRight(){
 		return right;
 	}
 	public String getUrl(){
 		return url;
 	}
 	public String getCls(){
 		return cls;
 	} 	
	public List<MenuItem> getItemList() {
		return itemList;
	}
	
	public boolean hasChilds() {
		return itemList.size()!=0;
	}

	public String getId() {
		return id;
	}

	/**
	 * @return the separator
	 */
	public boolean isSeparator() {
		return separator;
	}

	public boolean isWindow() {
		return window;
	}

	
}
