package reborncore.shields.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.annotation.Nullable;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.CountingOutputStream;

public class TextureDownloadUtils
{

	@Nullable
	ShieldTextureLoader shieldTextureLoader;

	public TextureDownloadUtils(ShieldTextureLoader shieldTextureLoader)
	{
		this.shieldTextureLoader = shieldTextureLoader;
	}

	public void downloadFile(String url, File target, String name) throws IOException
	{
		URL dl = new URL(url);
		if (!target.exists())
		{
			target.mkdirs();
		}
		File fl = new File(target, name);
		OutputStream os = null;
		InputStream is = null;
		ProgressListener progressListener = new ProgressListener();
		try
		{
			os = new FileOutputStream(fl);
			is = dl.openStream();

			DownloadCountingOutputStream dcount = new DownloadCountingOutputStream(os);
			dcount.setListener(progressListener);
			if (shieldTextureLoader != null)
			{
				shieldTextureLoader.steps = Integer.parseInt(dl.openConnection().getHeaderField("Content-Length"));
				shieldTextureLoader.showBar = true;
			}

			// begin transfer by writing to dcount, not os.
			IOUtils.copy(is, dcount);

		} catch (Exception e)
		{
			if (shieldTextureLoader != null)
			{
				shieldTextureLoader.showBar = false;
			}
			e.printStackTrace();
		} finally
		{
			if (os != null)
			{
				os.close();
				if (shieldTextureLoader != null)
				{
					shieldTextureLoader.showBar = false;
				}
			}
			if (is != null)
			{
				is.close();
				if (shieldTextureLoader != null)
				{
					shieldTextureLoader.showBar = false;
				}
			}
		}
		if (shieldTextureLoader != null)
		{
			shieldTextureLoader.showBar = false;
		}
	}

	private class ProgressListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (shieldTextureLoader != null)
			{
				shieldTextureLoader.step = (int) ((DownloadCountingOutputStream) e.getSource()).getByteCount();
			}
			if (shieldTextureLoader != null && shieldTextureLoader.step == shieldTextureLoader.steps)
			{
				shieldTextureLoader.showBar = false;
			}
		}
	}
}

class DownloadCountingOutputStream extends CountingOutputStream
{

	private ActionListener listener = null;

	public DownloadCountingOutputStream(OutputStream out)
	{
		super(out);
	}

	public void setListener(ActionListener listener)
	{
		this.listener = listener;
	}

	@Override
	protected void afterWrite(int n) throws IOException
	{
		super.afterWrite(n);
		if (listener != null)
		{
			listener.actionPerformed(new ActionEvent(this, 0, null));
		}
	}

}
