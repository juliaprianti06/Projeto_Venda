/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import factory.ConnectionFactory;
import gui.TabelaGUI;
import java.sql.*;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import modelo.Cliente;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author santo
 */
public class ClienteDAO {
    private Connection connection;
    
    public ClienteDAO(){
        this.connection = new ConnectionFactory().getConnection();
    }
    
    public void adiciona(Cliente cliente){
        String sql = "INSERT INTO cliente(cli_nome, cli_cpf, cli_email, cli_telefone, cli_endereco, cli_data_nascimento) Values(?,?,?,?,?,?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            // String id_aux=Integer.toString(cliente.getId());
             stmt.setString(1, cliente.getNome());
             stmt.setString(2, cliente.getCpf());
             stmt.setString(3, cliente.getEmail());
             stmt.setString(4, cliente.getTelefone());
             stmt.setString(5, cliente.getEndereco());
             if (cliente.getData_nascimento() != null){
              stmt.setDate(6, java.sql.Date.valueOf(cliente.getData_nascimento()));
        } else {
                stmt.setNull(6, java.sql.Types.DATE);
        }
             stmt.execute();
             stmt.close();
        }
        catch (SQLException u) {
            throw new RuntimeException(u);
        }
    }
    public List<Cliente> getLista() {
    List<Cliente> lista = new ArrayList<>();
    String sql = "SELECT * FROM cliente";

    try {
        PreparedStatement stmt = connection.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Cliente c = new Cliente();
            c.setCodigo(rs.getInt("cli_id"));
            c.setNome(rs.getString("cli_nome"));
            c.setCpf(rs.getString("cli_cpf"));
            c.setEmail(rs.getString("cli_email"));
            c.setTelefone(rs.getString("cli_telefone"));
            c.setEndereco(rs.getString("cli_endereco"));
            c.setData_nascimento(rs.getDate("cli_data_nascimento").toLocalDate());

            lista.add(c);
        }

        rs.close();
        stmt.close();
    } catch (SQLException e) {
        throw new RuntimeException("Erro ao listar clientes: " + e.getMessage(), e);
    }

    return lista;
}
    public List<Cliente> buscarPorNomeOuCpf(String busca) {
    List<Cliente> clientes = new ArrayList<>();
    String sql = "SELECT * FROM cliente WHERE cli_nome LIKE ? OR cli_cpf LIKE ?";

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setString(1, "%" + busca + "%");
        stmt.setString(2, "%" + busca + "%");
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Cliente cliente = new Cliente();
            cliente.setCodigo(rs.getInt("cli_id"));
            cliente.setNome(rs.getString("cli_nome"));
            cliente.setCpf(rs.getString("cli_cpf"));
            cliente.setEmail(rs.getString("cli_email"));
            cliente.setTelefone(rs.getString("cli_telefone"));
            cliente.setEndereco(rs.getString("cli_endereco"));
            cliente.setData_nascimento(rs.getDate("cli_data_nascimento").toLocalDate());
            clientes.add(cliente);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return clientes;
}
    
   public void deletarCliente(int codigoCliente) {
    String sql = "DELETE FROM cliente WHERE cli_id = ?";

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {

        System.out.println("Tentando excluir cliente com ID: " + codigoCliente);

        stmt.setInt(1, codigoCliente);
        
        int rowsAffected = stmt.executeUpdate();
        System.out.println("Linhas afetadas: " + rowsAffected);

        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(null, "Cliente excluído com sucesso!");
        } else {
            JOptionPane.showMessageDialog(null, "Cliente não encontrado para exclusão.");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro ao deletar cliente: " + e.getMessage());
        e.printStackTrace();
    }
}

public void deletarSelecionado(JTable tabelaGUI) {
    int linhaSelecionada = tabelaGUI.getSelectedRow();

    if (linhaSelecionada == -1) {
        JOptionPane.showMessageDialog(null, "Nenhuma linha selecionada.");
        return;
    }

    Object[] opcoes = {"Sim", "Não"};
    int resposta = JOptionPane.showOptionDialog(
            null,
            "Tem certeza que deseja excluir este cliente?",
            "Confirmar Exclusão",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            opcoes,
            opcoes[0] // opção padrão selecionada
    );

    if (resposta == JOptionPane.YES_OPTION) {
        int codigoCliente = (int) tabelaGUI.getValueAt(linhaSelecionada, 0);

        System.out.println("Excluindo cliente com código: " + codigoCliente);

        deletarCliente(codigoCliente);

        DefaultTableModel modelo = (DefaultTableModel) tabelaGUI.getModel();
        modelo.removeRow(linhaSelecionada);

        if (tabelaGUI.getParent() instanceof TabelaGUI tabelaGui) {
            tabelaGui.carregarTabela();
        }
    }

    else {
        
        JOptionPane.showMessageDialog(null, "Nenhuma linha selecionada.");
    }
}
}



