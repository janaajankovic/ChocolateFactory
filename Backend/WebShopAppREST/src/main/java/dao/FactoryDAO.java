package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import beans.Chocolate;
import beans.Factory;
import beans.Location;
import beans.roles.Manager;
import beans.roles.Worker;
import io.jsonwebtoken.Claims;
import utils.TokenUtils;

public class FactoryDAO {
	
	private ArrayList<Factory> factories = new ArrayList<>();
	private ChocolateDAO chocolateDAO;
	private LocationDAO locationDAO;
    private WorkerDAO workerDAO;
    private ManagerDAO managerDAO;

	private String contextPath;

	
	public FactoryDAO(String contextPath) {
		this.contextPath = contextPath;
		chocolateDAO = new ChocolateDAO(contextPath);
		locationDAO = new LocationDAO(contextPath);
		workerDAO = new WorkerDAO(contextPath);
		managerDAO = new ManagerDAO(contextPath);
		loadFactories(contextPath);
		loadChocolatesForFactories();
	}
	private void loadChocolatesForFactories() {
		for (Factory factory : factories) {
			ArrayList<Chocolate> chocolates = chocolateDAO.findChocolatesByFactoryId(factory.getId());
			factory.setAvailableChocolates(chocolates);
		}
	}
	public ArrayList<Factory> findAll() {
		loadFactories(contextPath);
		loadChocolatesForFactories();
		return factories;
	}

	public Factory findFactory(int id) {
		loadFactories(contextPath);
		loadChocolatesForFactories();
		for (Factory factory : factories) {
			if (factory.getId() == id) {
				return factory;
			}
		}
		return null;
	}
	
	public Chocolate addChocolateToFactory(int id, Chocolate chocolate) {
		loadFactories(contextPath);
		loadChocolatesForFactories();
		Factory f = findFactory(id);
		f.addChocolateToFactory(chocolate);
		return chocolate;
	}
	
	public Factory updateFactory(int id, Factory factory) {
		loadFactories(contextPath);
		loadChocolatesForFactories();
		Factory f = findFactory(id);
		if (f == null) {
			return save(factory);
		} else {
			f.setName(factory.getName());
			f.setWorktime(factory.getWorktime());
			f.setStatus(factory.isStatus());
			f.setLocation(factory.getLocation());
			f.setImage(factory.getImage());
			f.setGrade(factory.getGrade());
			return f;
		}
	}
	
	public Factory save(Factory factory) {
		
		loadFactories(contextPath);
        int maxId = -1;
        for (Factory f : factories) {
            if (f.getId() > maxId) {
                maxId = f.getId();
            }
        }
        maxId++;
        factory.setId(maxId);
        
        int maks = -1;
        for(Location l : locationDAO.findAll()) {
        	if (l.getId() > maxId) {
                maxId = l.getId();
            }
        }
        maxId++;
        
        factory.setLocation(maxId);
     
        String path = this.contextPath + "images\\factory" + maxId + ".jpg"; 

        return null; // Return the saved Chocolate object
	}
	
	public Factory deleteFactoryById(int id) {
		loadFactories(contextPath);
		loadChocolatesForFactories();
        Factory factoryToRemove = null;
        for (Factory factory : factories) {
            if (factory.getId() == id) {
                factoryToRemove = factory;
                break;
            }
        }
        if (factoryToRemove != null) {
            factories.remove(factoryToRemove);
        }
        return factoryToRemove;
    }

    // Metoda za filtriranje tvornica prema statusu (radi ili ne radi)
    public ArrayList<Factory> filterFactoriesByStatus(boolean status) {
    	loadFactories(contextPath);
		loadChocolatesForFactories();
        ArrayList<Factory> filteredFactories = new ArrayList<>();
        for (Factory factory : factories) {
            if (factory.isStatus() == status) {
                filteredFactories.add(factory);
            }
        }
        return filteredFactories;
    }

    // Metoda za pronalaženje filtriranih tvornica
    public ArrayList<Factory> findFilteredFactories() {
    	loadFactories(contextPath);
		loadChocolatesForFactories();
        // Ovdje implementirati logiku za pronalaženje filtriranih tvornica
        // Na primjer, možete filtrirati tvornice prema nekom kriteriju
        // Ovdje ćemo samo vratiti sve tvornice za demonstraciju
        return factories;
    }
    public Worker addWorkerToFactory(int factoryId, Worker worker) {
        loadFactories(contextPath);
        loadChocolatesForFactories();
        
        Factory factory = findFactory(factoryId);
        if (factory != null) {
            worker.setFactoryId(factoryId);
            workerDAO.save(worker, contextPath); // Save the worker with updated factory ID
            return worker;
        }
        return null;
    }
    public boolean isManagerOfFactory(String username, int factoryId) {
        loadFactories(contextPath);
        
        // Pronaći managera sa datim username-om
        Manager manager = null;
        for (Manager m : managerDAO.findAll()) {
            if (m.getUsername().equals(username)) {
                manager = m;
                break;
            }
        }
        
        // Provjeriti da li je pronađeni manager zadužen za traženu fabriku
        if (manager != null && manager.getFactoryId() == factoryId) {
            return true;
        }
        
        return false;
    }
	private void loadFactories(String contextPath) {
		this.factories = new ArrayList<Factory>();
		BufferedReader in = null;
		try {
			File file = new File(contextPath + "/factories.txt");
			in = new BufferedReader(new FileReader(file));
			String line;
			StringTokenizer st;
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.equals("") || line.indexOf('#') == 0)
					continue;
				st = new StringTokenizer(line, ";");
				int id = Integer.parseInt(st.nextToken().trim());
				String name = st.nextToken().trim();
				String worktime = st.nextToken().trim();
				boolean status = Boolean.parseBoolean(st.nextToken().trim());
				int locationId = Integer.parseInt(st.nextToken().trim());
				String image = st.nextToken().trim();
				double grade = Double.parseDouble(st.nextToken().trim());
				factories.add(new Factory(id, name, new ArrayList<>(), worktime, status, locationId, image, grade));
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}