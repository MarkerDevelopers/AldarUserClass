package Commands;


import com.huni.MightyMite.MightMite;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class classcommand  implements CommandExecutor  {


	@Override
	public boolean onCommand(CommandSender player,Command cmd,String label,String[] args) {
		if(player instanceof Player) {
			Player p = ((Player) player);

			if(p.isOp()) {
				if(args.length==1) {
					if(args[0].equalsIgnoreCase("����")) {
						if(MightMite.plugin.cooltime.containsKey(p.getName()+"isOP")) {
							MightMite.plugin.cooltime.remove(p.getName()+"isOP");
							p.sendMessage("��Ÿ���� ������� �ʽ��ϴ�.");
							return true;
						}
						MightMite.plugin.cooltime.put(p.getName()+"isOP", 1);
						p.sendMessage("��Ÿ���� ����˴ϴ�.");
						return true;
					}
					if(Bukkit.getPlayer(args[0])!=null) {
						Player target = Bukkit.getPlayer(args[0]);
						MightMite.plugin.CreateUserFile(target);
						p.sendMessage(target.getName()+"���� ����Ƽ ����Ʈ�� �����Ͽ����ϴ�.");
						target.sendMessage("�����ڿ� ���� ����Ƽ ����Ʈ�� �����Ͽ����ϴ�.");
						return true;
					}

				}
			}
			if(args.length==0) {

			}
		}
		return false;
	}

}
