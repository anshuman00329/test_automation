package db

import groovy.sql.Sql
import org.yaml.snakeyaml.Yaml

class DbConnectionFactory {

    def envParams
    def env_tag ='sc2020devint'
    def mysql_url
    def envDbParams

    DbConnectionFactory(){
        envParams = new Yaml().load(new FileReader(System.getProperty("user.dir") + '/src/test/groovy/config/tlm_env.yml'))[env_tag]
        envDbParams= envParams['db_config']
        if (envParams==null)
        {
            throw new Exception("No DB properties defined for the env_tag="+ env_tag  + "under config/tlm_env.yml, please check and retry")
        }
    }

    def raise_unknown_db_exception(db_config)
    {
        throw new Exception("Database name not found in the Yaml file, please check and retry")
    }


    def add_mysql_url(db_config)
    {
        if(db_config==null)
        {
            return raise_unknown_db_exception(db_config)
        }
        else
        {
            mysql_url = "jdbc:mysql://${db_config['host']}:${db_config['port']}/${db_config["service"]}"
            return mysql_url
        }
    }

    Sql acceptDbProperties() {
        def dbProperties = Sql.newInstance(add_mysql_url(envParams['db_config']['accept']), envDbParams['default']['username'], envDbParams['default']['password'], envDbParams['default']['driver'])
        return dbProperties
    }

    Sql tenderDbProperties() {
        def dbProperties = Sql.newInstance(add_mysql_url(envParams['db_config']['tender']), envDbParams['default']['username'], envDbParams['default']['password'], envDbParams['default']['driver'])
        return dbProperties
    }

    Sql shipmentDbProperties() {
        def dbProperties = Sql.newInstance(add_mysql_url(envParams['db_config']['shipment']), envDbParams['default']['username'], envDbParams['default']['password'], envDbParams['default']['driver'])
        return dbProperties
    }

    public void closeConnetion(Sql sql) {
        sql.close();
    }


}
